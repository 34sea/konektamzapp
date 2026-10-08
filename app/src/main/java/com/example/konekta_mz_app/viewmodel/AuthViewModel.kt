package com.example.konekta_mz_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.data.repository.AuthRepository
import com.example.konekta_mz_app.util.SessionManager
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

class AuthViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        if (sessionManager.isLoggedIn()) {
            val userId = sessionManager.getUserId()
            if (userId > 0) {
                viewModelScope.launch {
                    val user = authRepository.getUserById(userId)
                    if (user != null) {
                        _state.value = _state.value.copy(
                            isLoggedIn = true,
                            currentUser = user
                        )
                    } else {
                        sessionManager.clearSession()
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    sessionManager.saveSession(
                        userId = user.id,
                        name = user.name,
                        email = user.email,
                        role = user.role.name,
                        imagePath = user.profileImagePath
                    )
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
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        companyName: String = "",
        companyDescription: String = "",
        profileImagePath: String = ""
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.register(
                name = name,
                email = email,
                password = password,
                role = role,
                phone = phone,
                location = location,
                latitude = latitude,
                longitude = longitude,
                companyName = companyName,
                companyDescription = companyDescription,
                profileImagePath = profileImagePath)
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
        sessionManager.clearSession()
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
            sessionManager.saveSession(
                userId = user.id,
                name = user.name,
                email = user.email,
                role = user.role.name,
                imagePath = user.profileImagePath
            )
        }
    }

    class Factory(
        private val application: Application,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(application, authRepository) as T
        }
    }
}
