package com.example.konekta_mz_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.repository.CategoryRepository
import com.example.konekta_mz_app.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeState(
    val offers: List<JobOffer> = emptyList(),
    val categories: List<com.example.konekta_mz_app.data.local.entity.Category> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val error: String? = null
)

class HomeViewModel(
    private val jobRepository: JobRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                combine(
                    jobRepository.getAllActiveOffers(),
                    categoryRepository.getAllCategories()
                ) { offers, categories ->
                    _state.value = _state.value.copy(
                        offers = offers,
                        categories = categories,
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

    fun searchOffers(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isBlank()) {
                jobRepository.getAllActiveOffers().collect { offers ->
                    _state.value = _state.value.copy(offers = offers)
                }
            } else {
                jobRepository.searchOffers(query).collect { offers ->
                    _state.value = _state.value.copy(offers = offers)
                }
            }
        }
    }

    fun filterByCategory(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
        viewModelScope.launch {
            if (category == null) {
                jobRepository.getAllActiveOffers().collect { offers ->
                    _state.value = _state.value.copy(offers = offers)
                }
            } else {
                jobRepository.getOffersByCategory(category).collect { offers ->
                    _state.value = _state.value.copy(offers = offers)
                }
            }
        }
    }

    class Factory(
        private val jobRepository: JobRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(jobRepository, categoryRepository) as T
        }
    }
}
