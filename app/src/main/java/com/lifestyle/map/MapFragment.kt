package com.lifestyle.map

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.lifestyle.R
import com.lifestyle.main.LifestyleApplication
import com.lifestyle.user.UserData
import com.lifestyle.user.UserViewModel
import com.lifestyle.weather.WeatherFragment

/**
 * Fragment used to set location data, and also go to the maps to search for hikes
 */
class MapFragment : Fragment() {
    private val mUserViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireContext().applicationContext as LifestyleApplication).userRepository)
    }

    //variables used to temp store info before sending it to the UserViewModel
    private var textLocationLocal : TextLocation? = null
    private var locationLocal : Location? = null
    //UI element variables
    private var countryEditText : EditText? = null
    private var cityEditText : EditText? = null
    private var stateEditText : EditText? = null
    private var streetEditText : EditText? = null
    private var zipcodeEditText : EditText? = null
    private var gotoMapButton : Button? = null
    private var submitLocationButton : Button? = null
    private var useGPSLocationButton : Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Set up view model observer
        mUserViewModel.data.observe(this.viewLifecycleOwner) { userData ->
            onLocationUpdated(userData.textLocation, userData.locationName)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        countryEditText = view.findViewById<View>(R.id.CountryEditText) as EditText
        stateEditText = view.findViewById<View>(R.id.StateEditText) as EditText
        cityEditText = view.findViewById<View>(R.id.CityEditText) as EditText
        streetEditText = view.findViewById<View>(R.id.StreetEditText) as EditText
        zipcodeEditText = view.findViewById<View>(R.id.ZipCodeEditText) as EditText
        gotoMapButton = view.findViewById<View>(R.id.gotoMapButton) as Button
        useGPSLocationButton = view.findViewById<Button>(R.id.useGPSLocationButton)
        submitLocationButton = view.findViewById<View>(R.id.submitNewLocationButton) as Button

        //Set up handlers for text changes
        countryEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                textLocationLocal?.country = text?.toString()
            }
        }
        stateEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                textLocationLocal?.state = text?.toString()
            }
        }
        cityEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                textLocationLocal?.city = text?.toString()
            }
        }
        streetEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                textLocationLocal?.streetAddress = text?.toString()
            }
        }
        zipcodeEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                textLocationLocal?.zipCode = text?.toString()
            }
        }

        //Set go to map button handler
        gotoMapButton?.setOnClickListener { _ ->
            var addressText = parseStringLocation()
            var searchUri : Uri
            if(mUserViewModel.data.value?.location != null)
            {
                val lat = mUserViewModel.data.value?.location!!.latitude
                val long = mUserViewModel.data.value?.location!!.longitude
                searchUri = Uri.parse("geo:$lat,$long?q=hikes")
            }
            else
            {
                searchUri = Uri.parse("geo:0,0?q=hikes near $addressText")
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)
            try{
                startActivity(mapIntent)
            }catch(ex: ActivityNotFoundException){
                //Do error handling here
            }
        }
        //Set use gps location button handler
        useGPSLocationButton?.setOnClickListener { view ->
            activity?.let {
                mUserViewModel.updateLocation(it)
            }
        }
        //Set the click handler for the submit button
        submitLocationButton?.setOnClickListener { _ ->
            activity?.let {
                mUserViewModel.setLocationFromText(it, parseStringLocation(), parseShortStringLocation())
            }
        }

        return view
    }

    private fun onLocationUpdated(location: TextLocation?, locationName: String?) {
        if(locationName != null) {
            countryEditText?.setText(locationName)
        }
        if(location?.country != null) {
            countryEditText?.setText(location?.country.toString())
        }
        if(location?.state != null) {
            stateEditText?.setText(location?.state.toString())
        }
        if(location?.city != null) {
            cityEditText?.setText(location?.city.toString())
        }
        if(location?.streetAddress != null) {
            streetEditText?.setText(location?.streetAddress.toString())
        }
        if(location?.zipCode != null) {
            zipcodeEditText?.setText(location?.zipCode.toString())
        }

    }

    private fun parseStringLocation() : String {
        var retval = ""
        if(!textLocationLocal?.streetAddress.isNullOrBlank()) {
            val streetAddress = textLocationLocal?.streetAddress.toString()
            retval = "$retval $streetAddress"
        }
        if(!textLocationLocal?.city.isNullOrBlank()) {
            val city = textLocationLocal?.city.toString()
            retval = "$retval $city"
        }
        if(!textLocationLocal?.state.isNullOrBlank()) {
            val state = textLocationLocal?.state.toString()
            retval = "$retval $state"
        }
        if(!textLocationLocal?.zipCode.isNullOrBlank()) {
            val zipCode = textLocationLocal?.zipCode.toString()
            retval = "$retval $zipCode"
        }
        if(!textLocationLocal?.country.isNullOrBlank()) {
            val country = textLocationLocal?.country.toString()
            retval = "$retval $country"
        }
        return retval
    }

    private fun parseShortStringLocation() : String {
        var retval = ""
        if(!textLocationLocal?.city.isNullOrBlank()) {
            val city = textLocationLocal?.city.toString()
            retval = "$city"
        }
        if(!textLocationLocal?.state.isNullOrBlank()) {
            val state = textLocationLocal?.state.toString()
            retval = if(retval != "") {
                "$retval, $state"
            } else {
                "$state"
            }
        }
        if(!textLocationLocal?.country.isNullOrBlank()) {
            val country = textLocationLocal?.country.toString()
            retval = if(retval != "") {
                "$retval, $country"
            } else {
                "$country"
            }
        }
        return retval
    }

}
