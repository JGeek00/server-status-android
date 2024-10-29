package com.jgeek00.ServerStatus.views.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.NoPaddingAlertDialog
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.navigation.Routes
import com.jgeek00.ServerStatus.providers.ServerInstancesProvider
import com.jgeek00.ServerStatus.utils.createServerAddress
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun ServersSection(navigationController: NavHostController) {
    val serversProvider: ServerInstancesProvider = ServerInstancesProvider.getInstance()

    val servers by serversProvider.servers.collectAsState()

    SectionHeader(
        title = stringResource(R.string.servers),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    )
    if (servers.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                imageVector = Icons.Rounded.Dns,
                contentDescription = stringResource(R.string.servers),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .size(30.dp)
            )
            Text(
                text = "No saved servers",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    else {
        servers.map { server ->
            ServerItem(server)
        }
    }
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, top = 16.dp)
    ) {
        Button(
            onClick = {
                navigationController.navigate(Routes.SERVER_FORM)
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.create_server),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainer)
                )
                Text(stringResource(R.string.create_server))
            }
        }
    }
}

@Composable
fun ServerItem(server: ServerModel) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var errorDeleteServerAlert by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    ListTile(
        label = server.name,
        supportingText = createServerAddress(server.method, server.ipDomain, server.port, server.path),
        leadingIcon = Icons.Rounded.Dns,
        onClick = {},
        onLongClick = { showOptionsDialog = true }
    )
    if (showOptionsDialog) {
        NoPaddingAlertDialog(
            title = "Options",
            content = {
                Column(
                    Modifier.padding(0.dp)
                ) {
                    ListTile(
                        label = "Delete",
                        supportingText = "Delete this server instance",
                        leadingIcon = Icons.Rounded.Delete,
                        onClick = {
                            showOptionsDialog = false
                            showDeleteAlert = true
                        },
                        padding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            },
            onDismissRequest = {},
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showOptionsDialog = false }
                ) {
                    Text("Close")
                }
            }
        )
    }
    if (showDeleteAlert) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                )
            },
            title = {
                Text(
                    text = "Delete server",
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(text = "Are you sure you want to delete this server connection? This action cannot be reverted.")
            },
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAlert = false
                        coroutineScope.launch {
                            val result = ServerInstancesProvider.getInstance().deleteServer(server.id)
                            if (!result) {
                                errorDeleteServerAlert = true
                            }
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAlert = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    if (errorDeleteServerAlert) {
        AlertDialog(
            title = {
                Text(
                    text = "Error when deleting server",
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(text = "The server connection could not be deleted due to an error.")
            },
            onDismissRequest = {},
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { errorDeleteServerAlert = false }
                ) {
                    Text("Close")
                }
            }
        )
    }
}