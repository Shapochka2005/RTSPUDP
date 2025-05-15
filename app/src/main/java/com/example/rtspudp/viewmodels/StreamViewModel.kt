package com.example.rtspudp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rtspudp.database.entities.UserStream
import com.example.rtspudp.repository.StreamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StreamViewModel(
    private val repository: StreamRepository
) : ViewModel() {
    private val _streams = MutableStateFlow<List<UserStream>>(emptyList())
    val streams: StateFlow<List<UserStream>> = _streams.asStateFlow()

    fun loadStreamsForUser(userId: Int) = viewModelScope.launch {
        repository.getStreamsForUser(userId).collect { streamsList ->
            _streams.value = streamsList
        }
    }

    fun addStream(userId: Int, name: String, url: String, protocol: String) = viewModelScope.launch {
        repository.addStreamForUser(userId, name, url, protocol)
        loadStreamsForUser(userId)
    }

    fun deleteStream(stream: UserStream) = viewModelScope.launch {
        repository.deleteStream(stream)
        loadStreamsForUser(stream.userId)
    }

    class Factory(
        private val repository: StreamRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StreamViewModel(repository) as T
        }
    }
}