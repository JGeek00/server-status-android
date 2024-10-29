package com.jgeek00.ServerStatus.providers

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.services.DatabaseService

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

    var servers: List<ServerModel> = listOf()

    fun getServersFromDatabase(context: Context) {
        val serversResult = DatabaseService(context).getServers()
        if (serversResult != null) {
            servers = serversResult
        }
    }
}
