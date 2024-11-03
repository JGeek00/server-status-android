package com.jgeek00.ServerStatus.services

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.utils.createServerAddress
import com.jgeek00.ServerStatus.utils.transformStatusJSON
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiClient {
    private var client: HttpClient? = null
    private var serverAddress: String? = null

    fun setApiClientInstance(server: ServerModel) {
        client = HttpClient(Android) {
            install(ContentNegotiation) {
                gson {
                    // Configure Gson if needed
                }
            }
            if (server.useBasicAuth && server.basicAuthUser != null && server.basicAuthPassword != null) {
                install(Auth) {
                    basic {
                        username = server.basicAuthUser
                        password = server.basicAuthPassword
                    }
                }
            }
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
                val response = client!!.get {
                    url("${serverAddress!!}/status")
                    contentType(ContentType.Application.Json)
                }

                if (response.status.isSuccess()) {
                    val body = response.body<String>()
                    val formatted = transformStatusJSON(JsonParser.parseString(body))
                    return@withContext Gson().fromJson(formatted, StatusResult::class.java)
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
                return@withContext null
            }
        }
    }
}