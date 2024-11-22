package com.jgeek00.ServerStatus

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.di.DataStoreServiceEntryPoint
import com.jgeek00.ServerStatus.di.ServerInstancesRepositoryEntryPoint
import com.jgeek00.ServerStatus.navigation.AppNavigation
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.ui.theme.ServerStatusTheme
import com.jgeek00.ServerStatus.viewmodels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        val dataStoreService = (application as ServerStatusApplication).dataStoreService

        runBlocking {
            val onboardingCompleted = dataStoreService.getBooleanValue(DataStoreKeys.ONBOARDING_COMPLETED)
            NavigationManager.getInstance().onboardingCompleted.value = onboardingCompleted ?: false
        }

        setContent {
            val context = LocalContext.current

            val serverInstancesProvider = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    ServerInstancesRepositoryEntryPoint::class.java
                ).serverInstancesRepository
            }

            LaunchedEffect(key1 = Unit) {
                serverInstancesProvider.getServersFromDatabase()
            }

            val themeValue = dataStoreService.getString(DataStoreKeys.THEME_MODE).collectAsState(
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
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                ) {
                    AppNavigation()
                }
            }
        }
    }
}