package com.jgeek00.ServerStatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.providers.ServerInstancesProvider
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.services.DatabaseService
import com.jgeek00.ServerStatus.ui.theme.ServerStatusTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        DatabaseService.init(this)  // Init database
        DataStoreService.initialize(this) // Init datastore

        lifecycleScope.launch {
            ServerInstancesProvider.getInstance().getServersFromDatabase() // Load servers from database
        }

        setContent {
            val themeValue = DataStoreService.getInstance().getString(DataStoreKeys.THEME_MODE).collectAsState(
                Enums.Theme.SYSTEM_DEFINED.name).value ?: Enums.Theme.SYSTEM_DEFINED.name

            @Composable
            fun getDarkModeEnabled(themeValue: Enums.Theme): Boolean {
                return when (themeValue) {
                    Enums.Theme.SYSTEM_DEFINED -> isSystemInDarkTheme()
                    Enums.Theme.LIGHT -> false
                    Enums.Theme.DARK -> true
                }
            }

            ServerStatusTheme(
                darkTheme = getDarkModeEnabled(themeValue = Enums.Theme.valueOf(themeValue)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background),
                ) {
                    NavigationManager()
                }
            }
        }
    }
}