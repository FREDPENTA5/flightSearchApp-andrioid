package com.example.flightsearch.ui.flightsearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.preferences.PreferencesRepository
import com.example.flightsearch.data.preferences.ThemePreference
import com.example.flightsearch.data.repository.AirportSummary
import com.example.flightsearch.data.repository.FlightSearchRepository
import com.example.flightsearch.data.repository.FlightRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val repository: FlightSearchRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    var uiState by mutableStateOf(FlightSearchUiState())
        private set

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
            uiState = uiState.copy(isAppReady = true)
        }
        viewModelScope.launch {
            preferencesRepository.themePreference.collect { theme ->
                uiState = uiState.copy(themePreference = theme)
            }
        }
        viewModelScope.launch {
            repository.observeFavoriteRoutes().collect { favorites ->
                uiState = uiState.copy(favoriteRoutes = favorites)
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        uiState = uiState.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (newQuery.isBlank()) {
                uiState = uiState.copy(
                    suggestions = emptyList(),
                    selectedAirport = null,
                    routeResults = emptyList(),
                    isLoading = false
                )
                return@launch
            }
            delay(180)
            uiState = uiState.copy(
                selectedAirport = null,
                routeResults = emptyList(),
                isLoading = true
            )
            val airports = repository.searchAirports(newQuery)
            uiState = uiState.copy(suggestions = airports, isLoading = false)
        }
    }

    fun onClearQuery() {
        onQueryChanged("")
    }

    fun onAirportSelected(airport: AirportSummary) {
        viewModelScope.launch {
            uiState = uiState.copy(
                selectedAirport = airport,
                query = "${airport.iataCode} - ${airport.name}",
                suggestions = emptyList(),
                isLoading = true
            )
            val routes = repository.getTopRoutesFrom(airport.iataCode)
            uiState = uiState.copy(routeResults = routes, isLoading = false)
        }
    }

    fun onToggleFavorite(route: FlightRoute) {
        viewModelScope.launch {
            repository.toggleFavorite(route.departureCode, route.destinationCode)
        }
    }

    fun onThemePreferenceChanged(themePreference: ThemePreference) {
        viewModelScope.launch {
            preferencesRepository.setThemePreference(themePreference)
        }
    }
}

class FlightSearchViewModelFactory(
    private val repository: FlightSearchRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
            return FlightSearchViewModel(repository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
