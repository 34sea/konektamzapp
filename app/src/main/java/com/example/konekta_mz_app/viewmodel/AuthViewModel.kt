package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // Check if there's a logged-in user (in a real app, use DataStore/SharedPreferences)
        _state.value = _state.value.copy(isLoggedIn = false)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        error = null
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        phone: String = "",
        location: String = "",
        companyName: String = "",
        companyDescription: String = ""
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.register(
                name, email, password, role, phone, location,
                companyName = companyName, companyDescription = companyDescription
            )
            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Conta criada com sucesso! Faça login."
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }

    fun logout() {
        _state.value = AuthState()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _state.value = _state.value.copy(successMessage = null)
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            authRepository.updateUser(user)
            _state.value = _state.value.copy(currentUser = user)
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}
