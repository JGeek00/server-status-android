package com.jgeek00.ServerStatus.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.jgeek00.ServerStatus.models.ServerModel
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.services.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatusRepository @Inject constructor(
    private val context: Context,
    private val apiRepository: ApiClient
) {
    var selectedServer = mutableStateOf<ServerModel?>(null)

    private val _data = MutableStateFlow<List<StatusResult>>(emptyList())
    val data = _data.asStateFlow()
    val loading = mutableStateOf(true)
    val error = mutableStateOf(false)

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    fun fetchData() {
        coroutineScope.launch {
            val result = apiRepository.getStatus()
            if (result != null) {
                val newList = _data.value.toMutableList()
                newList += result
                _data.value = newList
                error.value = false
            }
            else {
                error.value = true
            }
            loading.value = false
        }
    }
}