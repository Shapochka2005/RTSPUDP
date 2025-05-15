package com.example.rtspudp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rtspudp.database.entities.User
import com.example.rtspudp.repository.StreamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: StreamRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUserId: Int get() = _currentUser.value?.id ?: 0

    fun login(username: String, password: String, onComplete: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(username, password)
            _currentUser.value = user
            if (user != null) {
                onComplete(true, user.isAdmin)
            } else {
                onComplete(false, false)
            }
        }
    }

    fun register(
        username: String,
        password: String,
        isAdmin: Boolean,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.registerUser(username, password, isAdmin)
                onComplete(true)
            } catch (_: Exception) {
                onComplete(false)
            }
        }
    }

    class Factory(private val repository: StreamRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(repository) as T
        }
    }
}