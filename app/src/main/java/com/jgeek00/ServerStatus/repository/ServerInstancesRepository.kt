package com.jgeek00.ServerStatus.repository

import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.services.DatabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ServerInstancesRepository @Inject constructor(
    private val databaseService: DatabaseService,
    private val dataStoreService: DataStoreService
) {
    private val _servers = MutableStateFlow<List<ServerModel>>(emptyList())
    val servers: StateFlow<List<ServerModel>> = _servers

    suspend fun getServersFromDatabase() {
        val serversResult = databaseService.getServers()
        if (serversResult != null) {
            _servers.value = serversResult
        }
    }

    suspend fun createServer(name: String, method: String, ipDomain: String, port: Int?, path: String?, useBasicAuth: Boolean, basicAuthUser: String?, basicAuthPassword: String?): Boolean {
        databaseService.createServer(name, method, ipDomain, port, path, useBasicAuth, basicAuthUser, basicAuthPassword)
            ?: return false

        val newServers = databaseService.getServers()
        if (newServers != null) {
            _servers.value = newServers
            if (newServers.size == 1) {
                setAsDefaultServer(serverId = newServers[0].id)
            }
            return true
        } else {
            return false
        }
    }

    suspend fun editServer(id: Int, name: String, method: String, ipDomain: String, port: Int?, path: String?, useBasicAuth: Boolean, basicAuthUser: String?, basicAuthPassword: String?): Boolean {
        val result = databaseService.updateServer(id, name, method, ipDomain, port, path, useBasicAuth, basicAuthUser, basicAuthPassword)
        if (result != null && result > 0) {
            val index = _servers.value.indexOfFirst { item -> item.id == id }
            val newServer = ServerModel(id, name, method, ipDomain, port, path, useBasicAuth, basicAuthUser, basicAuthPassword)
            _servers.value = _servers.value.toMutableList().apply { set(index, newServer) }
            return true
        }
        return false
    }

    suspend fun deleteServer(serverId: Int): Boolean {
        val result = databaseService.deleteServer(serverId)
        if (result) {
             val newServers = _servers.value.filter { item -> item.id != serverId }
            _servers.value = newServers

            val defaultServer = dataStoreService.getInt(DataStoreKeys.DEFAULT_SERVER).first()
            if (defaultServer == serverId) {
                if (newServers.isNotEmpty()) {
                    setAsDefaultServer(serverId = newServers[0].id)
                } else {
                    dataStoreService.removeInt(DataStoreKeys.DEFAULT_SERVER)
                }
            }
            return true

        } else {
            return false
        }
    }

    suspend fun setAsDefaultServer(serverId: Int) {
        dataStoreService.setInt(DataStoreKeys.DEFAULT_SERVER, serverId)
    }
}
