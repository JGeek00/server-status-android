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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.components.SwitchListTile
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.viewmodels.ServerFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerFormView(navigationController: NavController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val viewModel: ServerFormViewModel = viewModel()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Server Form") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigationController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.save() }
                    ) {
                        Image(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = "Save"
                        )
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
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                SectionHeader(
                    title = "Server information",
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
                    label = { Text("Server Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.serverNameError.value != null,
                    supportingText = {
                        if (viewModel.serverNameError.value != null) {
                            Text(viewModel.serverNameError.value!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                SectionHeader(
                    title = "Connection details",
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
                        ) {
                            Text("HTTP")
                        }
                        SegmentedButton(
                            selected = viewModel.connectionMethod.value === Enums.ConnectionMethod.HTTPS,
                            onClick = { viewModel.connectionMethod.value = Enums.ConnectionMethod.HTTPS },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        ) {
                            Text("HTTPS")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    label = { Text("IP address or domain") },
                    value = viewModel.ipDomain.value,
                    onValueChange = {
                        viewModel.ipDomainError.value = null
                        viewModel.ipDomain.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    isError = viewModel.ipDomainError.value != null,
                    supportingText = {
                        if (viewModel.ipDomainError.value != null) {
                            Text(viewModel.ipDomainError.value!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = viewModel.port.value,
                    onValueChange = {
                        viewModel.portError.value = null
                        viewModel.port.value = it
                    },
                    label = { Text("Port") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = viewModel.portError.value != null,
                    supportingText = {
                        if (viewModel.portError.value != null) {
                            Text(viewModel.portError.value!!, color = MaterialTheme.colorScheme.error)
                        }
                        else {
                            Text("Optional")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = viewModel.path.value,
                    onValueChange = {
                        viewModel.pathError.value = null
                        viewModel.path.value = it
                    },
                    label = { Text("Path") },
                    placeholder = { Text("Example: /status") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.pathError.value != null,
                    supportingText = {
                        if (viewModel.pathError.value != null) {
                            Text(viewModel.pathError.value!!, color = MaterialTheme.colorScheme.error)
                        }
                        else {
                            Text("Optional")
                        }
                    }
                )
                SectionHeader(
                    title = "Basic authentication",
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .padding(bottom = 8.dp, top = 36.dp)
                )
            }
            SwitchListTile(
                label = "Use basic authentication",
                checked = viewModel.useBasicAuth.value,
                onCheckedChange = { viewModel.useBasicAuth.value = it}
            )
            if (viewModel.useBasicAuth.value) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.basicAuthUsername.value,
                        onValueChange = {
                            viewModel.basicAuthUsernameError.value = null
                            viewModel.basicAuthUsername.value = it
                        },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.basicAuthUsernameError.value != null,
                        supportingText = {
                            if (viewModel.basicAuthUsernameError.value != null) {
                                Text(viewModel.basicAuthUsernameError.value!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = viewModel.basicAuthPassword.value,
                        onValueChange = {
                            viewModel.basicAuthPasswordError.value = null
                            viewModel.basicAuthPassword.value = it
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = viewModel.basicAuthPasswordError.value != null,
                        supportingText = {
                            if (viewModel.basicAuthPasswordError.value != null) {
                                Text(viewModel.basicAuthPasswordError.value!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }
            }
        }
    }
}