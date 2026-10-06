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

data class MapState(
    val offers: List<JobOffer> = emptyList(),
    val selectedOffer: JobOffer? = null,
    val isLoading: Boolean = true,
    val userLatitude: Double = -25.9692, // Maputo default
    val userLongitude: Double = 32.5732,
    val zoomLevel: Double = 19.0
)

class MapViewModel(private val jobRepository: JobRepository) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    init {
        loadOffers()
    }

    private fun loadOffers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            jobRepository.getAllActiveOffers().collect { offers ->
                _state.value = _state.value.copy(offers = offers, isLoading = false)
            }
        }
    }

    fun selectOffer(offer: JobOffer) {
        _state.value = _state.value.copy(selectedOffer = offer)
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        _state.value = _state.value.copy(userLatitude = latitude, userLongitude = longitude)
    }

    class Factory(private val jobRepository: JobRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MapViewModel(jobRepository) as T
        }
    }
}
