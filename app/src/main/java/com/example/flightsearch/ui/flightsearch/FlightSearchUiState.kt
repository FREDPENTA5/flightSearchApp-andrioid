package com.example.flightsearch.ui.flightsearch

import com.example.flightsearch.data.preferences.ThemePreference
import com.example.flightsearch.data.repository.AirportSummary
import com.example.flightsearch.data.repository.FlightRoute

data class FlightSearchUiState(
    val isAppReady: Boolean = false,
    val query: String = "",
    val suggestions: List<AirportSummary> = emptyList(),
    val selectedAirport: AirportSummary? = null,
    val routeResults: List<FlightRoute> = emptyList(),
    val favoriteRoutes: List<FlightRoute> = emptyList(),
    val isLoading: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.SYSTEM
)
