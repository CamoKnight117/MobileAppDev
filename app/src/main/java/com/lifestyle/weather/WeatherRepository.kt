package com.lifestyle.weather

import android.app.Application
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import com.lifestyle.database.WeatherDao
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.concurrent.Executors


class WeatherRepository(weatherDao: WeatherDao) {
    public val data : MutableLiveData<WeatherData> = MutableLiveData<WeatherData>()
    private var location : Location? = null

    public fun setLocation(location : Location) {
        this.location = location
        loadData()
    }

    public fun update() {
        loadData()
    }

    private fun loadData() {
        location?.let {
            FetchWeatherTask().execute(it)
        }
    }

    companion object {
        /** The unsecured key to our team's OpenWeather API access. This key provides API access, not account access.
         * Our account is free-tier; it has no billing info attached.
         * Our account has access to 60 calls per minute, to a maximum of 1,000,000 calls per month. */
        private const val INSECURE_OPENWEATHER_KEY = "cd31a8658a4169b5b89342953b4f940b"

        @Volatile
        private var instance : WeatherRepository? = null

        @Synchronized
        public fun getInstance(application: WeatherDao) : WeatherRepository {
            if(instance==null)
                instance = WeatherRepository(application)
            return instance!!
        }
    }

    inner class FetchWeatherTask {
        var executorService = Executors.newSingleThreadExecutor()
        var mainThreadHandler : Handler = HandlerCompat.createAsync(Looper.getMainLooper())

        public fun execute(location: Location) {
            executorService.execute {
                try {
                    val jsonData : String = URL("https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=cd31a8658a4169b5b89342953b4f940b").readText()
                    postToMainThread(jsonData)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun postToMainThread(jsonData : String) {
            mainThreadHandler.post {
                try {
                    data.value = Json{ignoreUnknownKeys=true}.decodeFromString<WeatherData>(jsonData)
                } catch(e : IllegalArgumentException) {
                    e.printStackTrace()
                } catch(e : SerializationException) {
                    e.printStackTrace()
                }

            }
        }
    }
}