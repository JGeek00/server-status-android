package com.jgeek00.ServerStatus.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.jgeek00.ServerStatus.constants.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreService(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(AppConfig.DATA_STORE_NAME)
    }

    fun getValue(key: Preferences.Key<Any>): Flow<Any?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key]
            }
    }

    suspend fun setValue(key: Preferences.Key<Any>, value: Any) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}