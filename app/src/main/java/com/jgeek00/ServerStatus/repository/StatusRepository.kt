package com.jgeek00.ServerStatus.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatusRepository @Inject constructor(
    private val context: Context,
    private val apiRepository: ApiRepository
) {
    val data = MutableStateFlow<List<String>>(emptyList())
    val loading = mutableStateOf(false)
    val error = mutableStateOf(false)

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    fun fetchData() {
        coroutineScope.launch {
            val result = apiRepository.getStatus()
        }
    }
}