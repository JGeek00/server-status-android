package com.jgeek00.ServerStatus.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.jgeek00.ServerStatus.constants.AppConfig
import com.jgeek00.ServerStatus.providers.NavigationProvider
import com.jgeek00.ServerStatus.services.DatabaseService.Companion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(AppConfig.DATA_STORE_NAME)

class DataStoreService private constructor(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        @Volatile
        private var INSTANCE: DataStoreService? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = DataStoreService(context)
                    }
                }
            }
        }

        fun getInstance(): DataStoreService {
            return INSTANCE ?: throw IllegalStateException("DataStoreManager not initialized. Call initialize() first.")
        }
    }

    fun getString(key: Preferences.Key<String>): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[key]
            }
    }

    suspend fun setString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}