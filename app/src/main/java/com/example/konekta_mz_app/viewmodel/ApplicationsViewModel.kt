package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.ApplicationStatus
import com.example.konekta_mz_app.data.local.entity.JobApplication
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.repository.AuthRepository
import com.example.konekta_mz_app.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ApplicationsState(
    val myApplications: List<JobApplication> = emptyList(),
    val jobApplications: List<JobApplication> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ApplicationsViewModel(
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ApplicationsState())
    val state: StateFlow<ApplicationsState> = _state.asStateFlow()

    fun loadMyApplications(candidateId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            jobRepository.getApplicationsByCandidate(candidateId).collect { apps ->
                _state.value = _state.value.copy(myApplications = apps, isLoading = false)
            }
        }
    }

    fun loadJobApplications(employerId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            jobRepository.getApplicationsByEmployer(employerId).collect { apps ->
                _state.value = _state.value.copy(jobApplications = apps, isLoading = false)
            }
        }
    }

    fun applyForJob(user: User, offer: JobOffer, coverLetter: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val existing = jobRepository.getApplication(user.id, offer.id)
                if (existing != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Já se candidatou a esta oferta"
                    )
                    return@launch
                }

                val application = JobApplication(
                    jobId = offer.id,
                    jobTitle = offer.title,
                    candidateId = user.id,
                    candidateName = user.name,
                    employerId = offer.employerId,
                    coverLetter = coverLetter
                )
                jobRepository.applyForJob(application)
                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Candidatura enviada com sucesso!"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateApplicationStatus(applicationId: Long, status: ApplicationStatus) {
        viewModelScope.launch {
            try {
                jobRepository.updateApplicationStatus(applicationId, status)
                _state.value = _state.value.copy(
                    successMessage = "Candidatura ${if (status == ApplicationStatus.ACCEPTED) "aceite" else "rejeitada"}"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }

    class Factory(
        private val jobRepository: JobRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ApplicationsViewModel(jobRepository, authRepository) as T
        }
    }
}
