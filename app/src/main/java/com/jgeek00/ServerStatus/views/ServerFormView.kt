package com.jgeek00.ServerStatus.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SettingsEthernet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.components.SwitchListTile
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.viewmodels.ServerFormViewModel
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerFormView(editServerId: String? = null) {
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModel: ServerFormViewModel = hiltViewModel()

    val statusRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            StatusRepositoryEntryPoint::class.java
        ).statusRepository
    }

    LaunchedEffect(Unit) {
        if (editServerId != null && editServerId.split("?").size == 2) {
            val id = editServerId.split("?")[1]
            viewModel.setServerData(serverId = id.toInt())
        }
    }

    val isActiveServer = if (viewModel.editingId.value != null) statusRepository.selectedServer.value?.id == viewModel.editingId.value else false

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
                title = { Text(text = if (viewModel.editingId.value != null) stringResource(R.string.edit_server) else stringResource(R.string.create_server)) },
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
                },
                actions = {
                    if (viewModel.saving.value) {
                        Box(
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 3.dp,
                                modifier = Modifier
                                    .size(22.dp)
                            )
                        }
                    }
                    else {
                        IconButton(
                            onClick = { viewModel.save() },
                            enabled = !viewModel.saving.value
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(padding)
                .windowInsetsPadding(if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) WindowInsets.displayCutout else WindowInsets(0.dp))
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                if (isActiveServer) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = stringResource(R.string.information),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.you_cannot_modify_the_connection_values_of_the_active_server_connection),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                SectionHeader(
                    title = stringResource(R.string.server_information),
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .padding(bottom = 16.dp, top = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.serverName.value,
                    onValueChange = {
                        viewModel.serverNameError.value = null
                        viewModel.serverName.value = it
                    },
                    label = { Text(stringResource(R.string.server_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    isError = viewModel.serverNameError.value != null,
                    supportingText = {
                        if (viewModel.serverNameError.value != null) {
                            Text(stringResource(viewModel.serverNameError.value!!), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !viewModel.saving.value
                )
                SectionHeader(
                    title = stringResource(R.string.connection_details),
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .padding(bottom = 16.dp, top = 36.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = viewModel.connectionMethod.value === Enums.ConnectionMethod.HTTP,
                            onClick = { viewModel.connectionMethod.value = Enums.ConnectionMethod.HTTP },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            enabled = !viewModel.saving.value && !isActiveServer
                        ) {
                            Text("HTTP")
                        }
                        SegmentedButton(
                            selected = viewModel.connectionMethod.value === Enums.ConnectionMethod.HTTPS,
                            onClick = { viewModel.connectionMethod.value = Enums.ConnectionMethod.HTTPS },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            enabled = !viewModel.saving.value && !isActiveServer
                        ) {
                            Text("HTTPS")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    label = { Text(stringResource(R.string.ip_address_or_domain)) },
                    value = viewModel.ipDomain.value,
                    onValueChange = {
                        viewModel.ipDomainError.value = null
                        viewModel.ipDomain.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    maxLines = 1,
                    isError = viewModel.ipDomainError.value != null,
                    supportingText = {
                        if (viewModel.ipDomainError.value != null) {
                            Text(stringResource(viewModel.ipDomainError.value!!), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    enabled = !viewModel.saving.value && !isActiveServer
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = viewModel.port.value,
                    onValueChange = {
                        viewModel.portError.value = null
                        viewModel.port.value = it
                    },
                    label = { Text(stringResource(R.string.port)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 1,
                    isError = viewModel.portError.value != null,
                    supportingText = {
                        if (viewModel.portError.value != null) {
                            Text(stringResource(viewModel.portError.value!!), color = MaterialTheme.colorScheme.error)
                        }
                        else {
                            Text(stringResource(R.string.optional))
                        }
                    },
                    enabled = !viewModel.saving.value && !isActiveServer
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = viewModel.path.value,
                    onValueChange = {
                        viewModel.pathError.value = null
                        viewModel.path.value = it
                    },
                    label = { Text(stringResource(R.string.path)) },
                    placeholder = { Text(stringResource(R.string.example_status)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.pathError.value != null,
                    supportingText = {
                        if (viewModel.pathError.value != null) {
                            Text(stringResource(viewModel.pathError.value!!), color = MaterialTheme.colorScheme.error)
                        }
                        else {
                            Text(stringResource(R.string.optional))
                        }
                    },
                    enabled = !viewModel.saving.value && !isActiveServer
                )
                SectionHeader(
                    title = stringResource(R.string.basic_authentication),
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .padding(bottom = 8.dp, top = 36.dp)
                )
            }
            SwitchListTile(
                label = stringResource(R.string.use_basic_authentication),
                checked = viewModel.useBasicAuth.value,
                onCheckedChange = { viewModel.useBasicAuth.value = it},
                enabled = !viewModel.saving.value && !isActiveServer
            )
            if (viewModel.useBasicAuth.value) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.basicAuthUsername.value,
                        onValueChange = {
                            viewModel.basicAuthUsernameError.value = null
                            viewModel.basicAuthUsername.value = it
                        },
                        label = { Text(stringResource(R.string.username)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        isError = viewModel.basicAuthUsernameError.value != null,
                        supportingText = {
                            if (viewModel.basicAuthUsernameError.value != null) {
                                Text(stringResource(viewModel.basicAuthUsernameError.value!!), color = MaterialTheme.colorScheme.error)
                            }
                        },
                        enabled = !viewModel.saving.value && !isActiveServer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.basicAuthPassword.value,
                        onValueChange = {
                            viewModel.basicAuthPasswordError.value = null
                            viewModel.basicAuthPassword.value = it
                        },
                        label = { Text(stringResource(R.string.password)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = viewModel.basicAuthPasswordError.value != null,
                        supportingText = {
                            if (viewModel.basicAuthPasswordError.value != null) {
                                Text(stringResource(viewModel.basicAuthPasswordError.value!!), color = MaterialTheme.colorScheme.error)
                            }
                        },
                        enabled = !viewModel.saving.value && !isActiveServer
                    )
                }
            }
        }
        if (viewModel.savingError.value) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = stringResource(R.string.error),
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.couldn_t_create_server_connection),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(text = stringResource(R.string.an_error_occurred_while_trying_to_create_a_server_connection))
                },
                onDismissRequest = {},
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.savingError.value = false }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
        if (viewModel.connectionError.value) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.SettingsEthernet,
                        contentDescription = stringResource(R.string.connection_error),
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.connection_error),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(text = stringResource(R.string.cannot_establish_a_connection_to_the_server_check_the_connection_values_and_try_again_if_your_server_is_behind_a_basic_authentication_make_sure_to_have_that_section_properly_configured))
                },
                onDismissRequest = {},
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.connectionError.value = false }
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
    }
}