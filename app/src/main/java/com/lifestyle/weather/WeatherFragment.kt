package com.lifestyle.weather

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lifestyle.R
import com.lifestyle.main.LifestyleApplication
import com.lifestyle.user.UserData
import com.lifestyle.user.UserViewModel
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
    private val mUserViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireContext().applicationContext as LifestyleApplication).userRepository)
    }

    private var locationTextView: TextView? = null
    private var weatherTextView: TextView? = null
    private var temperatureTextView: TextView? = null
    private var handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val weatherViewModel : WeatherViewModel by viewModels {
        WeatherViewModelFactory((activity?.application as LifestyleApplication).weatherRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set viewmodel observers.
        weatherViewModel.data.observe(this.viewLifecycleOwner) { weatherData: WeatherData ->
            timestampLastWeatherReply = System.currentTimeMillis()
            lastWeatherReply = weatherData
            putWeatherOnUI()
        }
        mUserViewModel.data.observe(this.viewLifecycleOwner) { userData ->
            onLocationUpdated(userData.location, userData.locationName)
            locationOnLastWeatherReply = userData.location
        }

        // Inflate the layout for this fragment
        val newView = inflater.inflate(R.layout.fragment_weather, container, false)

        // Gather Views.
        locationTextView = newView.findViewById(R.id.weatherLocationTextView)
        weatherTextView = newView.findViewById(R.id.weatherWeatherTextView)
        temperatureTextView = newView.findViewById(R.id.weatherTemperatureTextView)

        // Populate Views with data.
        //onLocationUpdated()
        if (lastWeatherReply != null)
            putWeatherOnUI()

        // Set up event handlers.
        locationTextView?.setOnClickListener { view ->
            activity?.let {
                mUserViewModel.updateLocation(it)
            }
        }

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

    private fun onLocationUpdated(location: Location?, locationName: String?) {
        locationTextView?.text = locationName ?: getString(R.string.none)
        location?.let { weatherViewModel.setLocation(it) }
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