package com.jgeek00.ServerStatus

import android.app.Application
import com.jgeek00.ServerStatus.services.DataStoreService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ServerStatusApplication: Application() {
    val dataStoreService: DataStoreService by lazy { DataStoreService(this) }
}