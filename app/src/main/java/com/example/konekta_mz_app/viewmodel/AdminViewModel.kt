package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.local.entity.JobApplication
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.data.repository.AuthRepository
import com.example.konekta_mz_app.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AdminState(
    val users: List<User> = emptyList(),
    val offers: List<JobOffer> = emptyList(),
    val applications: List<JobApplication> = emptyList(),
    val isLoading: Boolean = true,
    val totalUsers: Int = 0,
    val totalOffers: Int = 0,
    val totalApplications: Int = 0,
    val error: String? = null,
    val successMessage: String? = null
)

class AdminViewModel(
    private val authRepository: AuthRepository,
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                combine(
                    authRepository.getAllUsers(),
                    jobRepository.getAllOffers(),
                    jobRepository.getAllApplications()
                ) { users, offers, applications ->
                    _state.value = _state.value.copy(
                        users = users,
                        offers = offers,
                        applications = applications,
                        totalUsers = users.size,
                        totalOffers = offers.size,
                        totalApplications = applications.size,
                        isLoading = false
                    )
                }.collect { }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                authRepository.deleteUser(user)
                _state.value = _state.value.copy(successMessage = "Utilizador removido")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteOffer(offer: JobOffer) {
        viewModelScope.launch {
            try {
                jobRepository.deleteOffer(offer)
                _state.value = _state.value.copy(successMessage = "Oferta removida")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun updateUserRole(user: User, newRole: UserRole) {
        viewModelScope.launch {
            try {
                val updatedUser = user.copy(role = newRole)
                authRepository.updateUser(updatedUser)
                _state.value = _state.value.copy(successMessage = "Role atualizado")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val jobRepository: JobRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(authRepository, jobRepository) as T
        }
    }
}
