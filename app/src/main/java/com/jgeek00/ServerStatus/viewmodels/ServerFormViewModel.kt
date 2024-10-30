package com.jgeek00.ServerStatus.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.constants.RegExps
import com.jgeek00.ServerStatus.providers.NavigationProvider
import com.jgeek00.ServerStatus.providers.ServerInstancesProvider
import kotlinx.coroutines.launch

class ServerFormViewModel: ViewModel() {
    var editingId = mutableStateOf<Int?>(null)

    fun setServerData(serverId: Int) {
        val server = ServerInstancesProvider.getInstance().servers.value.find { item -> item.id == serverId }
        if (server == null) return

        serverName.value = server.name
        ipDomain.value = server.ipDomain
        port.value = if (server.port != null) server.port.toString() else ""
        path.value = server.path ?: ""
        useBasicAuth.value = server.useBasicAuth
        basicAuthUsername.value = server.basicAuthUser ?: ""
        basicAuthPassword.value = server.basicAuthPassword ?: ""

        editingId.value = serverId
    }

    var serverName = mutableStateOf("")
    var serverNameError = mutableStateOf<Int?>(null)

    var connectionMethod = mutableStateOf(Enums.ConnectionMethod.HTTP)

    var ipDomain = mutableStateOf("")
    var ipDomainError = mutableStateOf<Int?>(null)

    var port = mutableStateOf("")
    var portError = mutableStateOf<Int?>(null)

    var path = mutableStateOf("")
    var pathError = mutableStateOf<Int?>(null)

    var useBasicAuth = mutableStateOf(false)

    var basicAuthUsername = mutableStateOf("")
    var basicAuthUsernameError = mutableStateOf<Int?>(null)

    var basicAuthPassword = mutableStateOf("")
    var basicAuthPasswordError = mutableStateOf<Int?>(null)

    var saving = mutableStateOf(false)
    var savingError = mutableStateOf(false)

    private fun validateForm(): Boolean {
        var ret = true

        if (serverName.value.isEmpty()) {
            ret = false
            serverNameError.value = R.string.server_name_cannot_be_empty
        }
        else {
            serverNameError.value = null
        }

        if (ipDomain.value.isEmpty()) {
            ret = false
            ipDomainError.value = R.string.ip_address_or_domain_cannot_be_empty
        }
        else if (!RegExps.ipv4Address.matches(ipDomain.value) && !RegExps.ipv6Address.matches(ipDomain.value) && !RegExps.domain.matches(ipDomain.value)) {
            ret = false
            ipDomainError.value = R.string.invalid_ip_address_or_domain
        }
        else {
            ipDomainError.value = null
        }

        if (port.value.isNotEmpty() && !RegExps.port.matches(port.value)) {
            ret = false
            portError.value = R.string.invalid_port
        }
        else {
            portError.value = null
        }

        if (path.value.isNotEmpty() && !RegExps.path.matches(path.value)) {
            ret = false
            pathError.value = R.string.invalid_path
        }
        else {
            pathError.value = null
        }

        if (useBasicAuth.value && basicAuthUsername.value.isEmpty()) {
            ret = false
            basicAuthUsernameError.value = R.string.username_cannot_be_empty
        }
        else {
            basicAuthUsernameError.value = null
        }

        if (useBasicAuth.value && basicAuthPassword.value.isEmpty()) {
            ret = false
            basicAuthPasswordError.value = R.string.password_cannot_be_empty
        }
        else {
            basicAuthPasswordError.value = null
        }

        return ret
    }

    fun save() {
        val res = validateForm()
        if (!res) return

        viewModelScope.launch {
            saving.value = true
            if (editingId.value != null) {
                val result = ServerInstancesProvider.getInstance().editServer(
                    id = editingId.value!!,
                    name = serverName.value,
                    method = connectionMethod.value.toString().lowercase(),
                    ipDomain = ipDomain.value,
                    port = port.value.toIntOrNull(),
                    path = path.value,
                    useBasicAuth = useBasicAuth.value,
                    basicAuthUser = basicAuthUsername.value,
                    basicAuthPassword = basicAuthPassword.value
                )
                savingError.value = !result
                saving.value = false
                if (result) {
                    NavigationProvider.getInstance().popBack()
                }
            }
            else {
                val result = ServerInstancesProvider.getInstance().createServer(
                    name = serverName.value,
                    method = connectionMethod.value.toString().lowercase(),
                    ipDomain = ipDomain.value,
                    port = port.value.toIntOrNull(),
                    path = path.value,
                    useBasicAuth = useBasicAuth.value,
                    basicAuthUser = basicAuthUsername.value,
                    basicAuthPassword = basicAuthPassword.value
                )
                savingError.value = !result
                saving.value = false
                if (result) {
                    NavigationProvider.getInstance().popBack()
                }
            }
        }
    }
}