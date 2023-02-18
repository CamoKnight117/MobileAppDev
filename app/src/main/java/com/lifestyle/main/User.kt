package com.lifestyle.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.lifestyle.bmr.ActivityLevel

class User {
    var name: String? = null
    var age = 0
    var location: Location? = null
    var locationName: String? = null
        private set
    var height = 0
    var weight = 0
    var sex = Sex.UNASSIGNED
    var activityLevel = ActivityLevel()
    var profilePicture = "TODO"

    enum class Sex
    {
        MALE, FEMALE, UNASSIGNED
    }

    fun getDailyCalorieIntake() : Float {
        val bmrVal = calculateBMR()
        return if(bmrVal != 0f) {
            activityLevel.workoutCaloriesPerWeek()/7 + bmrVal
        } else {
            0f
        }
    }

    fun calculateBMR() : Float {
        return if(age != 0 && height != 0 && weight != 0 && sex != Sex.UNASSIGNED) {
            when (sex) {
                Sex.MALE -> 88.362f + (13.397f * weight) + (4.799f*height)-(5.677f*age)
                Sex.FEMALE -> (447.593f + (9.247f * weight) + (3.098f * height)-(4.330f * age))
                Sex.UNASSIGNED -> 0f
            }
        } else {
            0f
        }
    }

    fun calculateSedentaryCalNeed() : Float {
        val averageSedentaryCalBurn = 250/7
        return calculateBMR() + averageSedentaryCalBurn
    }

    fun calculateLightlyActiveCalNeed() : Float {
        val averageSedentaryCalBurn = 1000/7
        return calculateBMR() + averageSedentaryCalBurn
    }

    fun calculateActiveCalNeed() : Float {
        val averageSedentaryCalBurn = 2250/7
        return calculateBMR() + averageSedentaryCalBurn
    }

    fun calculateVeryActiveCalNeed() : Float {
        val averageSedentaryCalBurn = 4000/7
        return calculateBMR() + averageSedentaryCalBurn
    }

    /**
     * Sets this User's location to the device's most recent coarse location. Asks for coarse location permissions if needed.
     * @param activity The Activity context that should be responsible for the location permission and request.
     */
    public fun refreshLocation(activity: Activity, successCallback: (Location)->Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestCode = MainActivity.registerPermissionRequestCallback(object : MainActivity.Companion.PermissionRequestCallback {
                override fun invoke(activity: Activity, permissions: Array<out String>, grantResults: IntArray) {
                    setLocationToLastDeviceLocation(activity, successCallback)
                }
            })
            // Calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), requestCode)
            return
        }
        setLocationToLastDeviceLocation(activity, successCallback)
    }

    private fun setLocationToLastDeviceLocation(context: Context, successCallback: (Location) -> Unit) {
        try {
            LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { newLocation ->
                location = newLocation

                val geocoder: Geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(newLocation.latitude, newLocation.longitude, 1)
                if(addresses.size >= 1)
                    locationName = addresses[0].let {
                        it.locality +", "+ it.adminArea +", "+ it.countryName
                    }


                successCallback(newLocation)
            }
        } catch(e : SecurityException) {}
    }
}


