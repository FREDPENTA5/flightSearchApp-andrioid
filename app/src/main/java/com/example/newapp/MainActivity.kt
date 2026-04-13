package com.example.flightsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.local.FlightSearchDatabase
import com.example.flightsearch.data.preferences.UserPreferencesRepository
import com.example.flightsearch.data.repository.DefaultFlightSearchRepository
import com.example.flightsearch.ui.flightsearch.FlightSearchScreen
import com.example.flightsearch.ui.flightsearch.FlightSearchViewModel
import com.example.flightsearch.ui.flightsearch.FlightSearchViewModelFactory
import com.example.flightsearch.ui.theme.FlightSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FlightSearchDatabase.getInstance(this)
        val repository = DefaultFlightSearchRepository(database.flightSearchDao())
        val preferencesRepository = UserPreferencesRepository(this)

        setContent {
            val viewModel: FlightSearchViewModel = viewModel(
                factory = FlightSearchViewModelFactory(repository, preferencesRepository)
            )
            FlightSearchTheme(themePreference = viewModel.uiState.themePreference) {
                FlightSearchScreen(
                    uiState = viewModel.uiState,
                    onQueryChange = viewModel::onQueryChanged,
                    onClearQuery = viewModel::onClearQuery,
                    onSelectAirport = viewModel::onAirportSelected,
                    onToggleFavorite = viewModel::onToggleFavorite,
                    onThemePreferenceChange = viewModel::onThemePreferenceChanged
                )
            }
        }
    }
}