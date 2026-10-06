package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.Category
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.repository.CategoryRepository
import com.example.konekta_mz_app.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JobState(
    val myOffers: List<JobOffer> = emptyList(),
    val selectedOffer: JobOffer? = null,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class JobViewModel(
    private val jobRepository: JobRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JobState())
    val state: StateFlow<JobState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _state.value = _state.value.copy(categories = categories)
            }
        }
    }

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
                    successMessage = "Criada com sucesso!"
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
                    successMessage = "Atualizada com sucesso!"
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

    class Factory(
        private val jobRepository: JobRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return JobViewModel(jobRepository, categoryRepository) as T
        }
    }
}
