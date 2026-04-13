package com.example.flightsearch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirports(airports: List<AirportEntity>)

    @Query("SELECT COUNT(*) FROM airport")
    suspend fun getAirportCount(): Int

    @Query(
        """
        SELECT * FROM airport
        WHERE iata_code LIKE '%' || :query || '%'
           OR name LIKE '%' || :query || '%'
        ORDER BY passengers DESC
        LIMIT 20
        """
    )
    suspend fun searchAirports(query: String): List<AirportEntity>

    @Query("SELECT * FROM airport WHERE iata_code = :code LIMIT 1")
    suspend fun getAirportByCode(code: String): AirportEntity?

    @Query(
        """
        SELECT * FROM airport
        WHERE iata_code != :departureCode
        ORDER BY passengers DESC
        LIMIT 20
        """
    )
    suspend fun getTopDestinations(departureCode: String): List<AirportEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(route: FavoriteRouteEntity)

    @Query(
        """
        DELETE FROM favorite_route
        WHERE departure_code = :departureCode AND destination_code = :destinationCode
        """
    )
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    @Query("SELECT * FROM favorite_route ORDER BY id DESC")
    fun observeFavoriteRoutes(): Flow<List<FavoriteRouteEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM favorite_route
        WHERE departure_code = :departureCode AND destination_code = :destinationCode
        """
    )
    suspend fun isFavorite(departureCode: String, destinationCode: String): Int
}
