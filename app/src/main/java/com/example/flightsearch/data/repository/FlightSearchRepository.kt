package com.example.flightsearch.data.repository

import kotlinx.coroutines.flow.Flow

data class AirportSummary(
    val iataCode: String,
    val name: String
)

data class FlightRoute(
    val departureCode: String,
    val departureName: String,
    val destinationCode: String,
    val destinationName: String
)

interface FlightSearchRepository {
    suspend fun ensureSeedData()
    suspend fun searchAirports(query: String): List<AirportSummary>
    suspend fun getTopRoutesFrom(departureCode: String): List<FlightRoute>
    fun observeFavoriteRoutes(): Flow<List<FlightRoute>>
    suspend fun toggleFavorite(departureCode: String, destinationCode: String)
}
