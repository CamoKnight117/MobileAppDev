package com.lifestyle.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherTable: WeatherTable)

    @Query("DELETE FROM weather_table")
    suspend fun clearTable()

    @Query("SELECT * FROM weather_table")
    fun getAllWeather(): Flow<List<WeatherTable>>
}