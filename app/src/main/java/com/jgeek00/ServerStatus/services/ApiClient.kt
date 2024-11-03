package com.jgeek00.ServerStatus.services

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.utils.createServerAddress
import com.jgeek00.ServerStatus.utils.encodeCredentialsBasicAuth
import com.jgeek00.ServerStatus.utils.transformStatusJSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiClient {
    private var client: OkHttpClient? = null

    private var serverAddress: String? = null

    fun setApiClientInstance(server: ServerModel) {
        client = if (server.useBasicAuth && server.basicAuthUser != null && server.basicAuthPassword != null) {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Basic ${encodeCredentialsBasicAuth(server.basicAuthUser, server.basicAuthPassword)}")
                        .build()
                    chain.proceed(request)
                }
                .build()
        } else {
            OkHttpClient()
        }

        this.serverAddress = "${
            createServerAddress(
                server.method,
                server.ipDomain,
                server.port,
                server.path
            )
        }/api"
    }

    suspend fun getStatus(): StatusResult? {
        if (serverAddress == null || client == null) return null

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${serverAddress!!}/status")
                    .build()

                client!!.newCall(request).execute().use { res ->
                    if (!res.isSuccessful || res.body == null) return@withContext null
                    val body = res.body!!.string()
                    val formatted = transformStatusJSON(JsonParser.parseString(body))
                    return@withContext Gson().fromJson(formatted, StatusResult::class.java)
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
                return@withContext null
            }
        }
    }
}