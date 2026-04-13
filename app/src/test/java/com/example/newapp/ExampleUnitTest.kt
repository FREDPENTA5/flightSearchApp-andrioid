package com.example.flightsearch

import com.example.flightsearch.data.preferences.PreferencesRepository
import com.example.flightsearch.data.preferences.ThemePreference
import com.example.flightsearch.data.repository.AirportSummary
import com.example.flightsearch.data.repository.FlightRoute
import com.example.flightsearch.data.repository.FlightSearchRepository
import com.example.flightsearch.ui.flightsearch.FlightSearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun queryChange_updatesSuggestions_afterDebounce() = runTest {
        val repository = FakeFlightSearchRepository()
        val preferences = FakePreferencesRepository()
        val viewModel = FlightSearchViewModel(repository, preferences)

        viewModel.onQueryChanged("entebbe")
        advanceTimeBy(300)

        assertEquals(1, viewModel.uiState.suggestions.size)
        assertEquals("EBB", viewModel.uiState.suggestions.first().iataCode)
        Dispatchers.resetMain()
    }
}

private class FakeFlightSearchRepository : FlightSearchRepository {
    override suspend fun ensureSeedData() = Unit

    override suspend fun searchAirports(query: String): List<AirportSummary> {
        return if (query.contains("entebbe", ignoreCase = true)) {
            listOf(AirportSummary("EBB", "Entebbe International Airport"))
        } else {
            emptyList()
        }
    }

    override suspend fun getTopRoutesFrom(departureCode: String): List<FlightRoute> = emptyList()
    override fun observeFavoriteRoutes(): Flow<List<FlightRoute>> = flowOf(emptyList())
    override suspend fun toggleFavorite(departureCode: String, destinationCode: String) = Unit
}

private class FakePreferencesRepository : PreferencesRepository {
    override val themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    override suspend fun setThemePreference(themePreference: ThemePreference) = Unit
}