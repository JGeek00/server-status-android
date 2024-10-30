package com.jgeek00.ServerStatus.providers

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.services.DatabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ServerInstancesProvider: ViewModel() {
    companion object {
        private var INSTANCE: ServerInstancesProvider? = null

        fun getInstance(): ServerInstancesProvider {
            if (INSTANCE == null) {
                INSTANCE = ServerInstancesProvider()
            }
            return INSTANCE!!
        }
    }

    private val _servers = MutableStateFlow<List<ServerModel>>(emptyList())
    val servers: StateFlow<List<ServerModel>> = _servers

    suspend fun getServersFromDatabase() {
        val serversResult = DatabaseService.getInstance().getServers()
        if (serversResult != null) {
            _servers.value = serversResult
        }
    }

    suspend fun createServer(name: String, method: String, ipDomain: String, port: Int?, path: String?, useBasicAuth: Boolean, basicAuthUser: String?, basicAuthPassword: String?): Boolean {
        DatabaseService.getInstance().createServer(name, method, ipDomain, port, path, useBasicAuth, basicAuthUser, basicAuthPassword)
            ?: return false

        val newServers = DatabaseService.getInstance().getServers()
        if (newServers != null) {
            _servers.value = newServers
            return true
        } else {
            return false
        }
    }

    suspend fun editServer(id: Int, name: String, method: String, ipDomain: String, port: Int?, path: String?, useBasicAuth: Boolean, basicAuthUser: String?, basicAuthPassword: String?): Boolean {
        val result = DatabaseService.getInstance().updateServer(id, name, method, ipDomain, port, path, useBasicAuth, basicAuthUser, basicAuthPassword)
        if (result != null && result > 0) {
            val index = _servers.value.indexOfFirst { item -> item.id == id }
            val newServer = ServerModel(id, name, method, ipDomain, port, path, useBasicAuth, basicAuthUser, basicAuthPassword)
            _servers.value = _servers.value.toMutableList().apply { set(index, newServer) }
            return true
        }
        return false
    }

    suspend fun deleteServer(serverId: Int): Boolean {
        val result = DatabaseService.getInstance().deleteServer(serverId)
        if (result) {
             _servers.value = _servers.value.filter { item -> item.id != serverId }
            return true
        } else {
            return false
        }
    }
}