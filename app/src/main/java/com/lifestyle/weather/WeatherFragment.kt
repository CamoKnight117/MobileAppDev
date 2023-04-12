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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lifestyle.R
import com.lifestyle.main.MainActivity
import com.lifestyle.main.UserProvider
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
    private lateinit var weatherViewModel : WeatherViewModel

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

        // Initialize the viewmodel and set an observer.
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        weatherViewModel.data.observe(this.viewLifecycleOwner) { weatherData : WeatherData ->
            timestampLastWeatherReply = System.currentTimeMillis()
            lastWeatherReply = weatherData
            putWeatherOnUI()
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
                val weather = Json{ignoreUnknownKeys=true}.decodeFromString<WeatherData>(response)
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
        putWeatherOnUI()
        return newView
    }

    private fun putWeatherOnUI() {
        var weatherText     = ""
        var temperatureText = ""
        lastWeatherReply?.apply{
            weatherText = weather?.getOrNull(0)?.main ?: getString(R.string.weatherErrorMessage)
            val temperatureKelvin = lastWeatherReply?.main?.temp
            temperatureText = if(temperatureKelvin != null) {
                val temperatureFarenheit: Double = (temperatureKelvin - 273.15) * 9.0/5.0 + 32.0
                getString(R.string.temperatureFarenheit, temperatureFarenheit.roundToInt())
            } else
                getString(R.string.temperatureErrorMessage)
        }
        this.weatherTextView?.text     = weatherText
        this.temperatureTextView?.text = temperatureText
    }

    private fun onLocationUpdated() {
        locationTextView?.text = userProvider?.getUser()?.locationName ?: getString(R.string.none)
        userProvider?.getUser()?.location?.let { weatherViewModel.setLocation(it) }
    }

    companion object {
        // Weather query rate limiting is managed over the lifetime of the app by static fields:
        private const val weatherRefreshIntervalMillis = 1000 * 60 * 60
        private var lastWeatherCallThread: Thread? = null
        private var timestampLastWeatherReply: Long = 0
        private var locationOnLastWeatherReply : Location? = null
        private var lastWeatherReply: WeatherData? = null

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