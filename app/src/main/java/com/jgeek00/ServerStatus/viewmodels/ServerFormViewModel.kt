package com.jgeek00.ServerStatus.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.jgeek00.ServerStatus.constants.Enums
import com.jgeek00.ServerStatus.constants.RegExps

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

    fun validateForm() {
        if (serverName.value.isEmpty()) {
            serverNameError.value = "Server name cannot be empty"
        }
        else {
            serverNameError.value = null
        }

        if (ipDomain.value.isEmpty()) {
           ipDomainError.value = "IP address or domain cannot be empty"
        }
        else if (!RegExps.ipv4Address.matches(ipDomain.value) && !RegExps.ipv6Address.matches(ipDomain.value) && !RegExps.domain.matches(ipDomain.value)) {
            ipDomainError.value = "Invalid IP address or domain"
        }
        else {
            ipDomainError.value = null
        }

        if (port.value.isNotEmpty() && !RegExps.port.matches(port.value)) {
            portError.value = "Invalid port"
        }
        else {
            portError.value = null
        }

        if (path.value.isNotEmpty() && !RegExps.path.matches(path.value)) {
            pathError.value = "Invalid path"
        }
        else {
            pathError.value = null
        }

        if (useBasicAuth.value && basicAuthUsername.value.isEmpty()) {
            basicAuthUsernameError.value = "Username cannot be empty"
        }
        else {
            basicAuthUsernameError.value = null
        }

        if (useBasicAuth.value && basicAuthPassword.value.isEmpty()) {
            basicAuthPasswordError.value = "Password cannot be empty"
        }
        else {
            basicAuthPasswordError.value = null
        }
    }

    fun save() {
        validateForm()
    }
}