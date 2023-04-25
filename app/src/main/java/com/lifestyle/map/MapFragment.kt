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

    private val liveDataObserver: Observer<UserData> =
        Observer { userData ->
            onLocationUpdated(mUserViewModel.data.value?.textLocation)
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
            onLocationUpdated(userData.textLocation)
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

        onLocationUpdated(mUserViewModel.data.value?.textLocation)

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
            val geocoder: Geocoder = Geocoder(requireContext())
            val addresses = geocoder.getFromLocationName(addressText, 1)!!
            var searchUri : Uri? = null
            if(addresses.size != 0)
            {
                val lat = addresses[0].latitude
                val long = addresses[0].longitude
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
            val geocoder: Geocoder = Geocoder(requireContext())
            val stringLocation = parseStringLocation()
            try {
                val addresses = geocoder.getFromLocationName(stringLocation, 1)!!
                if(addresses.size != 0)
                {
                    locationLocal?.latitude = addresses[0].latitude
                    locationLocal?.longitude = addresses[0].longitude
                    textLocationLocal?.state = addresses[0].adminArea
                    textLocationLocal?.city = addresses[0].locality
                    textLocationLocal?.state = addresses[0].adminArea
                    textLocationLocal?.country = addresses[0].countryName
                    textLocationLocal?.zipCode = addresses[0].postalCode
                    textLocationLocal?.streetAddress = addresses[0].thoroughfare
                    setUserLocationFromTextLocation()
                }
            }
            catch (e: java.io.IOException)
            {
                //This occurs if there are no text inputs. Just do nothing in this case and carry on
            }
        }

        return view
    }

    private fun onLocationUpdated(location: TextLocation?) {
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

    //Interacts with the user view model
    private fun setUserLocationFromTextLocation(){
        textLocationLocal?.let { mUserViewModel.setTextLocation(it) }
        mUserViewModel.setLocationName(parseShortStringLocation())
        mUserViewModel.setLocationDirect(Location(parseShortStringLocation()))
    }

}
