package com.lifestyle.weather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.main.MainActivity
import com.lifestyle.main.UserProvider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.roundToInt

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
    private var weatherTextView: TextView? = null
    private var temperatureTextView: TextView? = null
    private var handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    /** A response from https://api.openweathermap.org/data/2.5/weather, minus "internal parameter"s, parsed from JSON.
     * See https://openweathermap.org/current#parameter for details.
     */
    @Serializable
    private class OpenWeatherCurrentWeatherReply(
        val coord: Coord? = null,
        val weather: List<Weather>? = null,
        val main: Main? = null,
        val visibility: Double? = null,
        val wind: Wind? = null,
        val rain: Rain? = null,
        val snow: Snow? = null,
        val dt: Double? = null,
        val sys: Sys? = null,
        val timezone: Double? = null
    ) {
        @Serializable
        public class Coord(val lon: Double? = null, val lat: Double? = null) { }
        @Serializable
        public class Weather(val id: Double? = null, val main: String? = null, val description: String? = null, val icon: String? = null) { }
        @Serializable
        public class Main(val temp: Double? = null, val feels_like: Double? = null, val temp_min: Double? = null, val temp_max: Double? = null, val pressure: Double? = null, val humidity: Double? = null, val sea_level: Double? = null, val grnd_level: Double? = null)
        @Serializable
        public class Wind(val speed: Double? = null, val deg: Double? = null, val gust: Double? = null) { }
        @Serializable
        public class Rain(@SerialName("1h") val oneH: Double? = null, @SerialName("3h") val threeH: Double? = null) { }
        @Serializable
        public class Snow(@SerialName("1h") val oneH: Double? = null, @SerialName("3h") val threeH: Double? = null) { }
        @Serializable
        public class Sys(val country: String? = null, val sunrise: Double? = null, val sunset: Double? = null) { }
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
        weatherTextView = newView.findViewById(R.id.weatherWeatherTextView)
        temperatureTextView = newView.findViewById(R.id.weatherTemperatureTextView)

        val user = userProvider?.getUser()

        // Populate Views with data.
        onLocationUpdated()
        if(lastWeatherReply != null)
            putWeatherOnUI()

        // Set up event handlers.
        locationTextView?.setOnClickListener { view ->
            activity?.let {
                user?.refreshLocation(it) { newLocation ->
                    onLocationUpdated()
                }
            }
        }

        // Start a weather API request if needed.
        sendWeatherRequestIfNeeded()

        return newView
    }

    private fun sendWeatherRequestIfNeeded() {
        // The minimum distance in meters a user's location must change before the weather should be re-queried.
        val minWeatherRefreshDistance : Float = 10000f
        val activity = requireActivity()
        val location = userProvider?.getUser()?.location
        val isLocationDifferent = (locationOnLastWeatherReply==null && location!=null)
                || (locationOnLastWeatherReply!=null && location!=null && location.distanceTo(locationOnLastWeatherReply!!) > minWeatherRefreshDistance+locationOnLastWeatherReply!!.accuracy+location.accuracy)
        if((lastWeatherCallThread?.isAlive != true)
            || System.currentTimeMillis() - timestampLastWeatherReply >= weatherRefreshIntervalMillis
            || isLocationDifferent
        ) {
            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                sendWeatherRequest(handler)
            } else {
                val requestCode = MainActivity.registerPermissionRequestCallback(object : MainActivity.Companion.PermissionRequestCallback {
                    override fun invoke(activity: Activity, permissions: Array<out String>, grantResults: IntArray) {
                        sendWeatherRequest(handler)
                    }
                })
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.INTERNET), requestCode)
            }
        }
    }

    private fun sendWeatherRequest(handler: Handler) {
        // Do not create more than one weather request thread at once.
        if(lastWeatherCallThread?.isAlive == true)
            return
        lastWeatherCallThread = thread() {
            var weatherText: String?
            var temperatureText: String?
            val userProviderVal = userProvider
            if(userProviderVal == null) {
                handler.post {
                    this.weatherTextView?.text     = getString(R.string.weatherErrorMessage)
                    this.temperatureTextView?.text = null
                }
                return@thread
            }
            val user = userProviderVal.getUser()
            val location = user.location
            if(location == null) {
                handler.post {
                    this.weatherTextView?.text     = null
                    this.temperatureTextView?.text = null
                }
                return@thread
            }
            val response: String = URL("https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=cd31a8658a4169b5b89342953b4f940b").readText() // .openConnection() as HttpURLConnection //URL("https", "api.openweathermap.org", -1, "/data/2.5/weather?lat=0&lon=0&appid=${INSECURE_OPENWEATHER_KEY}").openConnection()
            try {
                val weather = Json{ignoreUnknownKeys=true}.decodeFromString<OpenWeatherCurrentWeatherReply>(response)
                handler.post {
                    timestampLastWeatherReply = System.currentTimeMillis()
                    lastWeatherReply = weather
                    putWeatherOnUI()
                }
            } catch(e: SerializationException) {
                handler.post {
                    this.weatherTextView?.text     = getString(R.string.weatherErrorMessage)
                    this.temperatureTextView?.text = null
                }
            } catch(e: java.lang.IllegalArgumentException) {
                handler.post {
                    this.weatherTextView?.text     = getString(R.string.weatherErrorMessage)
                    this.temperatureTextView?.text = null
                }
            }
        }
    }

    private fun putWeatherOnUI() {
        val weatherText     = lastWeatherReply?.weather?.getOrNull(0)?.main ?: getString(R.string.weatherErrorMessage)
        val temperatureKelvin = lastWeatherReply?.main?.temp
        val temperatureText: String? = if(temperatureKelvin != null) {
            val temperatureFarenheit: Double = (temperatureKelvin - 273.15) * 9.0/5.0 + 32.0
            getString(R.string.temperatureFarenheit, temperatureFarenheit.roundToInt())
        } else
            getString(R.string.temperatureErrorMessage)
        this.weatherTextView?.text     = weatherText
        this.temperatureTextView?.text = temperatureText
    }

    private fun onLocationUpdated() {
        locationTextView?.text = userProvider?.getUser()?.locationName ?: getString(R.string.none)
        sendWeatherRequestIfNeeded()
    }

    companion object {
        /** The unsecured key to our team's OpenWeather API access. This key provides API access, not account access.
         * Our account is free-tier; it has no billing info attached.
         * Our account has access to 60 calls per minute, to a maximum of 1,000,000 calls per month. */
        private const val INSECURE_OPENWEATHER_KEY = "cd31a8658a4169b5b89342953b4f940b"
        // Weather query rate limiting is managed over the lifetime of the app by static fields:
        private const val weatherRefreshIntervalMillis = 1000 * 60 * 60
        private var lastWeatherCallThread: Thread? = null
        private var timestampLastWeatherReply: Long = 0
        private var locationOnLastWeatherReply : Location? = null
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