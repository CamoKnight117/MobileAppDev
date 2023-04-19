package com.lifestyle.weather

import android.app.Application
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import com.lifestyle.database.LifestyleDatabase
import com.lifestyle.database.UserDao
import com.lifestyle.database.WeatherDao
import com.lifestyle.database.WeatherTable
import com.lifestyle.main.LifestyleApplication
import com.lifestyle.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.net.URL
import java.util.concurrent.Executors


class WeatherRepository(val weatherDao: WeatherDao) {
    public val data : MutableLiveData<WeatherData> = MutableLiveData<WeatherData>()
    private var location : Location? = null
    private var jsonData : String? = null

    public fun setLocation(location : Location) {
        this.location = location
        update()
    }

    public fun update() {
        location?.let {
            mScope.launch(Dispatchers.IO){
                fetchAndParseWeatherData(it)
            }
        }
    }

    @WorkerThread
    private suspend fun insert() {
        data.value?.let { weatherData ->
            weatherData.coord?.let { coord ->
                val weatherTable = WeatherTable(weatherData.coord, Json.encodeToString(weatherData))
                weatherDao.insert(weatherTable)
            }
        }
    }

    @WorkerThread
    suspend fun fetchAndParseWeatherData(location: Location) {
        try {
            jsonData = URL("https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=cd31a8658a4169b5b89342953b4f940b").readText()
        } catch (e : Exception) {
            e.printStackTrace()
        }
        jsonData?.let {
            data.postValue(Json{ignoreUnknownKeys=true}.decodeFromString<WeatherData>(it))
            insert()
        }
    }

    companion object {
        /** The unsecured key to our team's OpenWeather API access. This key provides API access, not account access.
         * Our account is free-tier; it has no billing info attached.
         * Our account has access to 60 calls per minute, to a maximum of 1,000,000 calls per month. */
        private const val INSECURE_OPENWEATHER_KEY = "cd31a8658a4169b5b89342953b4f940b"

        @Volatile
        private var mInstance : WeatherRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        fun getInstance(weatherDao: WeatherDao, scope: CoroutineScope) : WeatherRepository {
            mScope = scope
            return mInstance ?: synchronized(this) {
                val instance = WeatherRepository(weatherDao)
                mInstance = instance
                instance
            }
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