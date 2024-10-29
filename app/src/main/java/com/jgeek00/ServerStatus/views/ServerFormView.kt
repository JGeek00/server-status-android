package com.jgeek00.ServerStatus.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.viewmodels.ServerFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerFormView(navigationController: NavController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val viewModel: ServerFormViewModel = viewModel()

    Scaffold(
        topBar = {
            LargeTopAppBar(
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
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = "Server information",
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = viewModel.serverName.value,
                onValueChange = { viewModel.serverName.value = it },
                label = { Text("Server Name") },
                modifier = Modifier.fillMaxWidth(),
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
                onValueChange = { viewModel.ipDomain.value = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = viewModel.port.value,
                onValueChange = { viewModel.port.value = it },
                label = { Text("Port") },
                supportingText = { Text("Optional") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = viewModel.path.value,
                onValueChange = { viewModel.path.value = it },
                label = { Text("Path") },
                supportingText = { Text("Optional") },
                placeholder = { Text("Example: /status") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}