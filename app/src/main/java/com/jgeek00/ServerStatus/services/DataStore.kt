package com.jgeek00.ServerStatus.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.jgeek00.ServerStatus.constants.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(AppConfig.DATA_STORE_NAME)

@Singleton
class DataStoreService @Inject constructor(context: Context) {
    private val dataStore = context.dataStore

    fun getString(key: Preferences.Key<String>): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[key]
            }
    }

    fun getInt(key: Preferences.Key<Int>): Flow<Int?> {
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

    suspend fun setInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun removeInt(key: Preferences.Key<Int>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}