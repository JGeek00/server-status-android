package com.jgeek00.ServerStatus.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.constants.RegExps
import com.jgeek00.ServerStatus.providers.ServerInstancesProvider
import kotlinx.coroutines.launch

class ServerFormViewModel: ViewModel() {
    var serverName = mutableStateOf("")
    var serverNameError = mutableStateOf<String?>(null)

    var connectionMethod = mutableStateOf(Enums.ConnectionMethod.HTTP)

    var ipDomain = mutableStateOf("")
    var ipDomainError = mutableStateOf<String?>(null)

    var port = mutableStateOf("")
    var portError = mutableStateOf<String?>(null)

    var path = mutableStateOf("")
    var pathError = mutableStateOf<String?>(null)

    var useBasicAuth = mutableStateOf(false)

    var basicAuthUsername = mutableStateOf("")
    var basicAuthUsernameError = mutableStateOf<String?>(null)

    var basicAuthPassword = mutableStateOf("")
    var basicAuthPasswordError = mutableStateOf<String?>(null)

    var saving = mutableStateOf(false)
    var savingError = mutableStateOf(false)

    private fun validateForm(): Boolean {
        var ret = true

        if (serverName.value.isEmpty()) {
            ret = false
            serverNameError.value = "Server name cannot be empty"
        }
        else {
            serverNameError.value = null
        }

        if (ipDomain.value.isEmpty()) {
            ret = false
            ipDomainError.value = "IP address or domain cannot be empty"
        }
        else if (!RegExps.ipv4Address.matches(ipDomain.value) && !RegExps.ipv6Address.matches(ipDomain.value) && !RegExps.domain.matches(ipDomain.value)) {
            ret = false
            ipDomainError.value = "Invalid IP address or domain"
        }
        else {
            ipDomainError.value = null
        }

        if (port.value.isNotEmpty() && !RegExps.port.matches(port.value)) {
            ret = false
            portError.value = "Invalid port"
        }
        else {
            portError.value = null
        }

        if (path.value.isNotEmpty() && !RegExps.path.matches(path.value)) {
            ret = false
            pathError.value = "Invalid path"
        }
        else {
            pathError.value = null
        }

        if (useBasicAuth.value && basicAuthUsername.value.isEmpty()) {
            ret = false
            basicAuthUsernameError.value = "Username cannot be empty"
        }
        else {
            basicAuthUsernameError.value = null
        }

        if (useBasicAuth.value && basicAuthPassword.value.isEmpty()) {
            ret = false
            basicAuthPasswordError.value = "Password cannot be empty"
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
        }
    }
}