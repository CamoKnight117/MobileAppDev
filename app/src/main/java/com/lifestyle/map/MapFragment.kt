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
import com.lifestyle.R
import com.lifestyle.main.UserProvider

/**
 * Fragment used to set location data, and also go to the maps to search for hikes
 */
class MapFragment : Fragment() {
    //variables used to get user info
    private var userProvider:UserProvider? = null
    private var location : TextLocation? = null
    //UI element variables
    private var countryEditText : EditText? = null
    private var cityEditText : EditText? = null
    private var stateEditText : EditText? = null
    private var streetEditText : EditText? = null
    private var zipcodeEditText : EditText? = null
    private var gotoMapButton : Button? = null
    private var submitLocationButton : Button? = null
    private var useGPSLocationButton : Button? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userProvider = try {
            context as UserProvider
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ${UserProvider::class}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)


        val user = userProvider!!.getUser()
        location = user.textLocation

        countryEditText = view.findViewById<View>(R.id.CountryEditText) as EditText
        stateEditText = view.findViewById<View>(R.id.StateEditText) as EditText
        cityEditText = view.findViewById<View>(R.id.CityEditText) as EditText
        streetEditText = view.findViewById<View>(R.id.StreetEditText) as EditText
        zipcodeEditText = view.findViewById<View>(R.id.ZipCodeEditText) as EditText
        gotoMapButton = view.findViewById<View>(R.id.gotoMapButton) as Button
        useGPSLocationButton = view.findViewById<View>(R.id.useGPSLocationButton) as Button
        submitLocationButton = view.findViewById<View>(R.id.submitNewLocationButton) as Button

        onLocationUpdated()

        //Set up handlers for text changes
        countryEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.textLocation.country = text?.toString()
            }
        }
        stateEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.textLocation.state = text?.toString()
            }
        }
        cityEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.textLocation.city = text?.toString()
            }
        }
        streetEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.textLocation.streetAddress = text?.toString()
            }
        }
        zipcodeEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.textLocation.zipCode = text?.toString()
            }
        }

        //Set go to map button handler
        gotoMapButton?.setOnClickListener { _ ->
            //The button press should open a camera
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
                user.refreshLocation(it) { newLocation ->
                    onLocationUpdated()
                    updateUserLocation()
                }
            }
        }
        //Set the click handler for the submit button
        submitLocationButton?.setOnClickListener { _ ->
            val geocoder: Geocoder = Geocoder(requireContext())
            val stringLocation = parseStringLocation()
            val addresses = geocoder.getFromLocationName(stringLocation, 1)!!
            if(addresses.size != 0)
            {
                user.textLocation.city = addresses[0].locality
                user.textLocation.state = addresses[0].adminArea
                user.textLocation.country = addresses[0].countryName
                user.textLocation.zipCode = addresses[0].postalCode
                user.textLocation.streetAddress = addresses[0].thoroughfare
                onLocationUpdated()
                updateUserLocation()
            }
        }

        return view
    }


    private fun onLocationUpdated() {
        if(location?.country != null) {
            countryEditText!!.setText(location?.country.toString())
        }
        if(location?.state != null) {
            stateEditText!!.setText(location?.state.toString())
        }
        if(location?.city != null) {
            cityEditText!!.setText(location?.city.toString())
        }
        if(location?.streetAddress != null) {
            streetEditText!!.setText(location?.streetAddress.toString())
        }
        if(location?.zipCode != null) {
            zipcodeEditText!!.setText(location?.zipCode.toString())
        }
    }

    private fun parseStringLocation() : String {
        var retval = ""
        if(!location?.streetAddress.isNullOrBlank()) {
            val streetAddress = location?.streetAddress.toString()
            retval = "$retval $streetAddress"
        }
        if(!location?.city.isNullOrBlank()) {
            val city = location?.city.toString()
            retval = "$retval $city"
        }
        if(!location?.state.isNullOrBlank()) {
            val state = location?.state.toString()
            retval = "$retval $state"
        }
        if(!location?.zipCode.isNullOrBlank()) {
            val zipCode = location?.zipCode.toString()
            retval = "$retval $zipCode"
        }
        if(!location?.country.isNullOrBlank()) {
            val country = location?.country.toString()
            retval = "$retval $country"
        }
        return retval
    }

    private fun parseShortStringLocation() : String {
        var retval = ""
        if(!location?.city.isNullOrBlank()) {
            val city = location?.city.toString()
            retval = "$city"
        }
        if(!location?.state.isNullOrBlank()) {
            val state = location?.state.toString()
            retval = if(retval != "") {
                "$retval, $state"
            } else {
                "$state"
            }
        }
        if(!location?.country.isNullOrBlank()) {
            val country = location?.country.toString()
            retval = if(retval != "") {
                "$retval, $country"
            } else {
                "$country"
            }
        }
        return retval
    }

    private fun updateUserLocation(){
        val user = userProvider!!.getUser()
        user.locationName = parseShortStringLocation()
        user.location = Location(parseShortStringLocation()) //Not sure if this works
    }

}
