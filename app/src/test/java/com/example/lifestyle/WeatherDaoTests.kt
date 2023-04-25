package com.example.lifestyle

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lifestyle.database.LifestyleDatabase
import com.lifestyle.database.WeatherDao
import com.lifestyle.database.WeatherTable
import com.lifestyle.weather.WeatherData
import kotlinx.coroutines.flow.toList
import java.io.IOException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach

/**
 * Class for testing weather DAO. Tests are inspired by tests from https://developer.android.com/training/data-storage/room/testing-db
 */
class WeatherDaoTests {
    private lateinit var weatherDao: WeatherDao
    private lateinit var db: LifestyleDatabase

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LifestyleDatabase::class.java).build()
        weatherDao = db.weatherDao()
    }

    @AfterEach
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    suspend fun writeAndReadUser() {
        val weather = WeatherTable(WeatherData.Coord(1.1, 1.2), "json")
        weatherDao.insert(weather)
        val weatherFromDatabase = weatherDao.getAllWeather().toList()[0][0]
        assertEquals(weatherFromDatabase.coord.lat, weather.coord.lat)
        assertEquals(weatherFromDatabase.coord.lon, weather.coord.lon)
        assertEquals(weatherFromDatabase.weatherJson, weather.weatherJson)
    }
}