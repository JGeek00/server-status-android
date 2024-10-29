package com.jgeek00.ServerStatus.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.jgeek00.ServerStatus.constants.Enums

class ServerFormViewModel: ViewModel() {
    var serverName = mutableStateOf("")
    var connectionMethod = mutableStateOf(Enums.ConnectionMethod.HTTP)
    var ipDomain = mutableStateOf("")
    var port = mutableStateOf("")
    var path = mutableStateOf("")
}