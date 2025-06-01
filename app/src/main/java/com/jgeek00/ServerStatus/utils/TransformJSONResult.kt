package com.jgeek00.ServerStatus.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun transformStatusJSON(input: JsonElement): JsonElement {
    val output = JsonObject()

    if (input.isJsonObject) {
        val jsonObject = input.asJsonObject

        val cpu = jsonObject.getAsJsonObject("cpu")
        if (cpu != null) {
            val coresData = JsonArray()

            val temperatures = cpu.getAsJsonObject("temperatures")
            val frequencies = cpu.getAsJsonObject("frequencies")

            val sortedFrequencies = frequencies?.entrySet()
                ?.map { entry ->
                    val cpuNumber = entry.key.removePrefix("cpu").toIntOrNull()
                    cpuNumber to entry
                }
                ?.filter { it.first != null }
                ?.sortedBy { it.first }
                ?.associateTo(LinkedHashMap()) { it.second.key to it.second.value }

            sortedFrequencies?.forEach { (index, values) ->
                val coreData = JsonObject()
                val coreIndex = index.replace("cpu", "")

                val cpuTemps = cpu.getAsJsonObject("temperatures")
                if (cpuTemps.has("Tctl")) {
                    val temp = cpuTemps.getAsJsonArray("Tctl")
                    if (temp != null) {
                        coreData.add("temperatures", temp)
                    }
                    coreData.add("frequencies", values)
                    coresData.add(coreData)
                }
                else {
                    val coreTemperature = temperatures?.getAsJsonArray("Core $coreIndex")
                    if (coreTemperature != null) {
                        coreData.add("temperatures", coreTemperature)
                    }
                    coreData.add("frequencies", JsonObject().apply {
                        values.asJsonObject.entrySet().forEach { (k, v) ->
                            addProperty(k, v.asInt)
                        }
                    })
                    coresData.add(coreData)
                }
            }

            cpu.remove("frequencies")
            cpu.remove("temperatures")

            output.add("cpu", JsonObject().apply {
                cpu.entrySet().forEach { (key, value) ->
                    add(key, value)
                }
                add("cpuCores", coresData)
            })
        }

        val memory = jsonObject.getAsJsonObject("memory")
        output.add("memory", JsonObject().apply {
            memory.entrySet().forEach { (key, value) ->
                add(key, value)
            }
        })

        val storage = jsonObject.getAsJsonObject("storage")
        if (storage != null && storage.isJsonObject) {
            val convertedStorage = JsonArray()

            storage.entrySet().forEach { (key, value) ->
                val storageItem = JsonObject()
                storageItem.addProperty("name", key)
                value.asJsonObject.entrySet().forEach { (innerKey, innerValue) ->
                    storageItem.add(innerKey, innerValue)
                }
                convertedStorage.add(storageItem)
            }

            output.add("storage", convertedStorage)
        }

        val network = jsonObject.getAsJsonObject("network")
        output.add("network", JsonObject().apply {
            network.entrySet().forEach { (key, value) ->
                add(key, value)
            }
        })

        val host = jsonObject.getAsJsonObject("host")
        output.add("host", JsonObject().apply {
            host.entrySet().forEach { (key, value) ->
                add(key, value)
            }
        })
    }

    return output
}