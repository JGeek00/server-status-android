package com.jgeek00.ServerStatus.views.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.di.DataStoreServiceEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun ThemeBox(theme: Enums.Theme) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dataStoreService = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            DataStoreServiceEntryPoint::class.java
        ).dataStoreService
    }

    val darkModeValue = dataStoreService.getString(DataStoreKeys.THEME_MODE).collectAsState(
        Enums.Theme.SYSTEM_DEFINED.name).value as String?

    when(theme) {
        Enums.Theme.SYSTEM_DEFINED -> {
            ThemeButton(
                iconId = R.drawable.smartphone_icon,
                text = stringResource(R.string.system),
                isEnabled = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.SYSTEM_DEFINED else true
            ) {
                scope.launch {
                    dataStoreService.setString(DataStoreKeys.THEME_MODE, Enums.Theme.SYSTEM_DEFINED.name)
                }
            }
        }
        Enums.Theme.LIGHT -> {
            ThemeButton(
                iconId = R.drawable.light_mode_icon,
                text = stringResource(R.string.light),
                isEnabled = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.LIGHT else false
            ) {
                scope.launch {
                    dataStoreService.setString(DataStoreKeys.THEME_MODE, Enums.Theme.LIGHT.name)
                }
            }
        }
        Enums.Theme.DARK -> {
            ThemeButton(
                iconId = R.drawable.dark_mode_icon,
                text = stringResource(R.string.dark),
                isEnabled = if (darkModeValue !== null) Enums.Theme.valueOf(darkModeValue) == Enums.Theme.DARK else false
            ) {
                scope.launch {
                    dataStoreService.setString(DataStoreKeys.THEME_MODE, Enums.Theme.DARK.name)
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
            contentColor = if (isEnabled) MaterialTheme.colorScheme.surfaceContainer else Color.Gray,
        ),
        modifier = Modifier
            .size(width = 120.dp, height = 100.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(iconId),
                contentDescription = text,
                colorFilter = ColorFilter.tint(color = if (isEnabled) MaterialTheme.colorScheme.surfaceContainer else Color.Gray),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(text)
        }
    }
}

@Composable
@Preview
fun ThemeButtonPreview() {
    ThemeButton(R.drawable.smartphone_icon, "System", true) {}
}