package com.jgeek00.ServerStatus.views.Settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.constants.Urls
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.navigation.Routes
import com.jgeek00.ServerStatus.utils.getAppVersion
import com.jgeek00.ServerStatus.utils.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView() {
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(stringResource(R.string.settings))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            NavigationManager.getInstance().popBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
                .windowInsetsPadding(
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) WindowInsets.displayCutout else WindowInsets(
                        0.dp
                    )
                )
                .padding(padding)
        ) {
            ServersSection()

            SectionHeader(title = stringResource(R.string.theme))
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
                title = stringResource(R.string.about_the_app),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
            if (context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName != "com.android.vending") {
                ListTile(
                    label = stringResource(R.string.give_a_tip_to_the_developer),
                    supportingText = stringResource(R.string.contribute_with_the_development_of_the_application),
                    onClick = {
                        openUrl(context, Urls.PAYPAL_DONATIONS)
                    }
                )
            }
            ListTile(
                label = stringResource(R.string.contact_the_developer),
                supportingText = stringResource(R.string.contact_form),
                onClick = { openUrl(context, Urls.APP_SUPPORT) }
            )
            getAppVersion(LocalContext.current)?.let { value ->
                ListTile(label = stringResource(R.string.app_version), supportingText = value)
            }
            ListTile(stringResource(R.string.created_by), "JGeek00")
        }
    }
}

