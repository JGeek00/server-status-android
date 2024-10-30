package com.jgeek00.ServerStatus.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jgeek00.ServerStatus.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(private val apiRepository: ApiRepository): ViewModel() {
    val data = MutableStateFlow<List<String>>(emptyList())
    val loading = mutableStateOf(false)
    val error = mutableStateOf(false)

    fun fetchData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = apiRepository.getStatus()
            }
        }
    }
}