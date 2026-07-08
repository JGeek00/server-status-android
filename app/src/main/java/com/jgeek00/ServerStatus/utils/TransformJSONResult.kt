package com.jgeek00.ServerStatus.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlin.math.roundToInt

fun transformStatusJSON(input: JsonElement): JsonElement {
    val output = JsonObject()

    if (input.isJsonObject) {
        val jsonObject = input.asJsonObject

        val cpu = jsonObject.getAsJsonObject("cpu")
        if (cpu != null) {
            val coresData = JsonArray()

            val temperatures = cpu.getAsJsonObject("temperatures")
            val frequencies = cpu.getAsJsonObject("frequencies")

            val toInts: (JsonElement?) -> JsonArray? = { element ->
                val arr = element?.takeIf { it.isJsonArray }?.asJsonArray
                if (arr == null || arr.size() == 0) null
                else JsonArray().apply { arr.forEach { add(it.asDouble.roundToInt()) } }
            }

            val genericTemp: JsonArray? = toInts(temperatures?.get("Tctl")) ?: run {
                val firsts = mutableListOf<Int>()
                val lasts = mutableListOf<Int>()
                temperatures?.entrySet()?.forEach { (_, value) ->
                    val arr = value.takeIf { it.isJsonArray }?.asJsonArray
                    if (arr != null && arr.size() >= 2) {
                        firsts.add(arr[0].asDouble.roundToInt())
                        lasts.add(arr[arr.size() - 1].asDouble.roundToInt())
                    }
                }
                if (firsts.isNotEmpty() && lasts.isNotEmpty()) {
                    JsonArray().apply {
                        add(firsts.max())
                        add(lasts.max())
                    }
                } else null
            }

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
                    coreData.add("frequencies", values)
                    coresData.add(coreData)
                }
                else {
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
                genericTemp?.let { add("temperature", it) }
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