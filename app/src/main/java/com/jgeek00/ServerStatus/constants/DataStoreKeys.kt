package com.jgeek00.ServerStatus.constants

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class DataStoreKeys {
    companion object {
        val THEME_MODE = stringPreferencesKey("DARK_MODE")
        val DEFAULT_SERVER = intPreferencesKey("DEFAULT_SERVER")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("ONBOARDING_COMPLETED")
    }
}