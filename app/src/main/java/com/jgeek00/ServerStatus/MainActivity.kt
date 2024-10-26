package com.jgeek00.ServerStatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.datastore.preferences.core.Preferences
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.ui.theme.ServerStatusTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = DataStoreService(this)

        setContent {
            val darkModeValue = dataStore.getValue(DataStoreKeys.THEME_MODE as Preferences.Key<Any>).collectAsState(
                Enums.Theme.SYSTEM_DEFINED.name).value as String?

            ServerStatusTheme(
                darkTheme = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.DARK else false,
            ) {
                NavigationManager()
            }
        }
    }
}