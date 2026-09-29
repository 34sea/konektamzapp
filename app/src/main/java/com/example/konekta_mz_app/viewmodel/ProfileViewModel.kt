package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val user = authRepository.getUserById(userId)
            _state.value = _state.value.copy(user = user, isLoading = false)
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                authRepository.updateUser(user)
                _state.value = _state.value.copy(
                    isLoading = false,
                    user = user,
                    successMessage = "Perfil atualizado com sucesso!"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(authRepository) as T
        }
    }
}
