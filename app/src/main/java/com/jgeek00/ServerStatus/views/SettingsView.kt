package com.jgeek00.ServerStatus.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.services.DataStoreService
import com.jgeek00.ServerStatus.utils.getAppVersion
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(navigationController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigationController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(padding)
        ) {
            SectionHeader(title = "Theme")
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ThemeBox(Enums.Theme.SYSTEM_DEFINED)
                ThemeBox(Enums.Theme.LIGHT)
                ThemeBox(Enums.Theme.DARK)
            }

            SectionHeader(
                title = "About the app",
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
            getAppVersion(LocalContext.current)?.also { value ->
                ListTile(label = "App version", supportingText = value)
            }
            ListTile("Created by", "JGeek00")
        }
    }
}

@Composable
fun ThemeBox(theme: Enums.Theme) {
    val scope = rememberCoroutineScope()
    val dataStore = DataStoreService(LocalContext.current)

    val darkModeValue = dataStore.getValue(DataStoreKeys.THEME_MODE as Preferences.Key<Any>).collectAsState(
        Enums.Theme.SYSTEM_DEFINED.name).value as String?

    when(theme) {
        Enums.Theme.SYSTEM_DEFINED -> {
            ThemeButton(
                iconId = R.drawable.smartphone_icon,
                text = "System",
                isEnabled = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.SYSTEM_DEFINED else true
            ) {
                scope.launch {
                    dataStore.setValue(DataStoreKeys.THEME_MODE, Enums.Theme.SYSTEM_DEFINED.name)
                }
            }
        }
        Enums.Theme.LIGHT -> {
            ThemeButton(
                iconId = R.drawable.light_mode_icon,
                text = "Light",
                isEnabled = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.LIGHT else false
            ) {
                scope.launch {
                    dataStore.setValue(DataStoreKeys.THEME_MODE, Enums.Theme.LIGHT.name)
                }
            }
        }
        Enums.Theme.DARK -> {
            ThemeButton(
                iconId = R.drawable.dark_mode_icon,
                text = "Dark",
                isEnabled = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.DARK else false
            ) {
                scope.launch {
                    dataStore.setValue(DataStoreKeys.THEME_MODE, Enums.Theme.DARK.name)
                }
            }
        }
    }
}

@Composable
fun ThemeButton(iconId: Int, text: String, isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f),
            contentColor = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else Color.Gray,
        ),
        modifier = Modifier
            .size(width = 100.dp, height = 100.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(iconId),
                contentDescription = "System defined",
                colorFilter = ColorFilter.tint(color = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else Color.Gray),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(text)
        }
    }
}