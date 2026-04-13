package com.example.flightsearch.data.repository

import com.example.flightsearch.data.local.FavoriteRouteEntity
import com.example.flightsearch.data.local.FlightSearchDao
import com.example.flightsearch.data.local.seedAirports
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultFlightSearchRepository(
    private val dao: FlightSearchDao
) : FlightSearchRepository {
    private val cityAliasToAirportCode = mapOf(
        "kampala" to "EBB",
        "uganda" to "EBB",
        "uga" to "EBB",
        "nairobi" to "NBO",
        "kenya" to "NBO",
        "kigali" to "KGL",
        "rwanda" to "KGL",
        "addis" to "ADD",
        "ethiopia" to "ADD"
    )

    override suspend fun ensureSeedData() {
        if (dao.getAirportCount() == 0) {
            dao.insertAirports(seedAirports)
        }
    }

    override suspend fun searchAirports(query: String): List<AirportSummary> {
        val normalized = query.trim()
        if (normalized.isBlank()) return emptyList()
        val directMatches = dao.searchAirports(normalized)
        val aliasCode = cityAliasToAirportCode[normalized.lowercase()]
        val aliasMatch = aliasCode?.let { dao.getAirportByCode(it) }

        return (directMatches + listOfNotNull(aliasMatch))
            .distinctBy { it.iataCode }
            .map { AirportSummary(it.iataCode, it.name) }
    }

    override suspend fun getTopRoutesFrom(departureCode: String): List<FlightRoute> {
        val departure = dao.getAirportByCode(departureCode) ?: return emptyList()
        return dao.getTopDestinations(departureCode).map { destination ->
            FlightRoute(
                departureCode = departure.iataCode,
                departureName = departure.name,
                destinationCode = destination.iataCode,
                destinationName = destination.name
            )
        }
    }

    override fun observeFavoriteRoutes(): Flow<List<FlightRoute>> {
        return dao.observeFavoriteRoutes().map { favorites ->
            favorites.mapNotNull { favorite ->
                resolveFavoriteRoute(favorite)
            }
        }
    }

    override suspend fun toggleFavorite(departureCode: String, destinationCode: String) {
        if (dao.isFavorite(departureCode, destinationCode) > 0) {
            dao.deleteFavorite(departureCode, destinationCode)
        } else {
            dao.insertFavorite(FavoriteRouteEntity(departureCode = departureCode, destinationCode = destinationCode))
        }
    }

    private suspend fun resolveFavoriteRoute(favorite: FavoriteRouteEntity): FlightRoute? {
        val departure = dao.getAirportByCode(favorite.departureCode) ?: return null
        val destination = dao.getAirportByCode(favorite.destinationCode) ?: return null
        return FlightRoute(
            departureCode = departure.iataCode,
            departureName = departure.name,
            destinationCode = destination.iataCode,
            destinationName = destination.name
        )
    }
}
