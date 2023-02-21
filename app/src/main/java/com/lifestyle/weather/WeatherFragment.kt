package com.lifestyle.weather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.main.MainActivity
import com.lifestyle.main.UserProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : Fragment() {
    private var userProvider: UserProvider? = null
    private var locationTextView: TextView? = null

    /** A response from https://api.openweathermap.org/data/2.5/weather, minus "internal parameter"s, parsed from JSON.
     * See https://openweathermap.org/current#parameter for details.
     */
    @Serializable
    private class OpenWeatherCurrentWeatherReply(
        val coord: Coord,
        val weather: List<Weather>,
        val main: Main,
        val visibility: Double,
        val wind: Wind,
        val rain: Rain,
        val snow: Snow,
        val dt: Double,
        val sys: Sys,
        val timezone: Double
    ) {
        @Serializable
        public class Coord(val lon: Double, val lat: Double) { }
        @Serializable
        public class Weather(val id: Double, val main: String, val description: String, val icon: String) { }
        @Serializable
        public class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Double, val humidity: Double, val sea_level: Double, val grnd_level: Double)
        @Serializable
        public class Wind(val speed: Double, val deg: Double, val gust: Double) { }
        @Serializable
        public class Rain(@SerialName("1h") val oneH: Double, @SerialName("3h") val threeH: Double) { }
        @Serializable
        public class Snow(@SerialName("1h") val oneH: Double, @SerialName("3h") val threeH: Double) { }
        @Serializable
        public class Sys(val country: String, val sunrise: Double, val sunset: Double) { }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userProvider = try {
            context as UserProvider
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ${UserProvider::class}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val newView = inflater.inflate(R.layout.fragment_weather, container, false)

        // Gather Views.
        locationTextView = newView.findViewById(R.id.weatherLocationTextView)

        val user = userProvider?.getUser()

        // Populate Views with data.
        onLocationUpdated()

        // Set up event handlers.
        locationTextView?.setOnClickListener { view ->
            activity?.let {
                user?.refreshLocation(it) { newLocation ->
                    onLocationUpdated()
                }
            }
        }

        // Start a weather API request if needed.
        val activity = requireActivity()
        if(!sentWeatherCall || System.currentTimeMillis() - timestampLastWeatherCall >= weatherRefreshIntervalMillis) {
            timestampLastWeatherCall = System.currentTimeMillis()
            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                sendWeatherRequest()
            } else {
                val requestCode = MainActivity.registerPermissionRequestCallback(object : MainActivity.Companion.PermissionRequestCallback {
                    override fun invoke(activity: Activity, permissions: Array<out String>, grantResults: IntArray) {
                        sendWeatherRequest()
                    }
                })
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.INTERNET), requestCode)
            }
        }

        return newView
    }

    private fun sendWeatherRequest() {
        thread() {
            val response: String = URL("https://api.openweathermap.org/data/2.5/weather?lat=0&lon=0&appid=cd31a8658a4169b5b89342953b4f940b").readText() // .openConnection() as HttpURLConnection //URL("https", "api.openweathermap.org", -1, "/data/2.5/weather?lat=0&lon=0&appid=${INSECURE_OPENWEATHER_KEY}").openConnection()
            try {
                val weather = Json.decodeFromString<OpenWeatherCurrentWeatherReply>(response)

            } catch(e: SerializationException) {
                // TODO: Display a message to the user.
            } catch(e: java.lang.IllegalArgumentException) {
                // TODO: Display a message to the user.
            }
        }
    }

    private fun onLocationUpdated() {
        locationTextView?.text = userProvider?.getUser()?.locationName ?: getString(R.string.none)
    }

    companion object {
        /** The unsecured key to our team's OpenWeather API access. This key provides API access, not account access.
         * Our account is free-tier; it has no billing info attached.
         * Our account has access to 60 calls per minute, to a maximum of 1,000,000 calls per month. */
        private const val INSECURE_OPENWEATHER_KEY = "cd31a8658a4169b5b89342953b4f940b"
        // Weather query rate limiting is managed over the lifetime of the app by static fields:
        private const val weatherRefreshIntervalMillis = 1000 * 60 * 60
        private var sentWeatherCall = false
        private var timestampLastWeatherCall: Long = 0
        private var lastWeatherReply: OpenWeatherCurrentWeatherReply? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeatherFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}