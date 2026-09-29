package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JobState(
    val myOffers: List<JobOffer> = emptyList(),
    val selectedOffer: JobOffer? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class JobViewModel(private val jobRepository: JobRepository) : ViewModel() {

    private val _state = MutableStateFlow(JobState())
    val state: StateFlow<JobState> = _state.asStateFlow()

    fun loadMyOffers(employerId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            jobRepository.getOffersByEmployer(employerId).collect { offers ->
                _state.value = _state.value.copy(myOffers = offers, isLoading = false)
            }
        }
    }

    fun loadOffer(offerId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val offer = jobRepository.getOfferById(offerId)
            _state.value = _state.value.copy(selectedOffer = offer, isLoading = false)
        }
    }

    fun createOffer(offer: JobOffer) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                jobRepository.createOffer(offer)
                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Oferta criada com sucesso!"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateOffer(offer: JobOffer) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                jobRepository.updateOffer(offer)
                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Oferta atualizada com sucesso!"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteOffer(offer: JobOffer) {
        viewModelScope.launch {
            jobRepository.deleteOffer(offer)
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }

    class Factory(private val jobRepository: JobRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return JobViewModel(jobRepository) as T
        }
    }
}
