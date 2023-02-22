package com.lifestyle.map

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
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
    private var location : Location? = null
    //UI element variables
    private var countryEditText : EditText? = null
    private var cityEditText : EditText? = null
    private var stateEditText : EditText? = null
    private var streetEditText : EditText? = null
    private var zipcodeEditText : EditText? = null
    private var gotoMapButton : Button? = null

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
        location = user.location

        countryEditText = view.findViewById<View>(R.id.CountryEditText) as EditText
        stateEditText = view.findViewById<View>(R.id.StateEditText) as EditText
        cityEditText = view.findViewById<View>(R.id.CityEditText) as EditText
        streetEditText = view.findViewById<View>(R.id.StreetEditText) as EditText
        zipcodeEditText = view.findViewById<View>(R.id.ZipCodeEditText) as EditText
        gotoMapButton = view.findViewById<View>(R.id.gotoMapButton) as Button

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

        //Set up handlers for text changes

        countryEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.location.country = text?.toString()
            }
        }
        stateEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.location.state = text?.toString()
            }
        }
        cityEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.location.city = text?.toString()
            }
        }
        streetEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.location.streetAddress = text?.toString()
            }
        }
        zipcodeEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.location.zipCode = text?.toString()
            }
        }

        gotoMapButton?.setOnClickListener { _ ->
            //The button press should open a camera
            var addressText = ""

            if(location?.streetAddress != null) {
                val streetAddress = location?.streetAddress.toString()
                addressText = "$addressText $streetAddress"
            }
            if(location?.city != null) {
                val city = location?.city.toString()
                addressText = "$addressText $city"
            }
            if(location?.state != null) {
                val state = location?.state.toString()
                addressText = "$addressText $state"
            }
            if(location?.zipCode != null) {
                val zipCode = location?.zipCode.toString()
                addressText = "$addressText $zipCode"
            }
            if(location?.country != null) {
                val country = location?.country.toString()
                addressText = "$addressText $country"
            }

            val searchUri = Uri.parse("geo:0,0?q=hikes near $addressText")
            val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)
            try{
                startActivity(mapIntent)
            }catch(ex: ActivityNotFoundException){
                //Do error handling here
            }
        }

        return view
    }
}