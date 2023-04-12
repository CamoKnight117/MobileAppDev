package com.lifestyle.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class UserViewModel(repository: UserRepository) : ViewModel() {
    private val jsonData: LiveData<UserData> = repository.data
    private val userRepository : UserRepository = repository

    val data: LiveData<UserData>
        get() = jsonData

    fun getDailyCalorieIntake() : Float {
        val bmrVal = calculateBMR()
        return if(bmrVal != 0f) {
            data.value?.activityLevel!!.workoutCaloriesPerWeek()/7 + bmrVal
        } else {
            0f
        }
    }

    /**
     * Calculates a user's BMR using the formula (found at https://www.garnethealth.org/news/basal-metabolic-rate-calculator#:~:text=Your%20basal%20metabolism%20rate%20is,4.330%20x%20age%20in%20years)
     * Men: BMR = 88.362 + (13.397 x weight in kg) + (4.799 x height in cm) – (5.677 x age in years)
     * Women: BMR = 447.593 + (9.247 x weight in kg) + (3.098 x height in cm) – (4.330 x age in years)
     * However, it's been adjusted to use imperial units instead, so it assumes that weight is in pounds and height is in inches
     */
    fun calculateBMR() : Float {
        return if(data.value?.age != 0 && data.value?.height != 0.0f && data.value?.weight != 0.0f && data.value?.sex != Sex.UNASSIGNED) {
            when (data.value?.sex!!) {
                Sex.MALE -> 88.362f + (29.535f * data.value?.weight!!) + (1.889f*data.value?.height!!)-(5.677f*data.value?.age!!)
                Sex.FEMALE -> (447.593f + (20.386f * data.value?.weight!!) + (1.220f * data.value?.height!!)-(4.330f * data.value?.age!!))
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

    override fun toString(): String {
        var s = StringBuilder()
        s.appendLine("Name: ${data.value?.name}")
        s.appendLine("Age: ${data.value?.age}")
        s.appendLine("Location: ${data.value?.locationName}")
        s.appendLine("Height: ${data.value?.height}")
        s.appendLine("Weight: ${data.value?.weight}")
        s.appendLine("Sex: ${data.value?.sex}")
        s.appendLine("CalPerHour: ${data.value?.activityLevel?.caloriesPerHour}")
        s.appendLine("WorkoutLength: ${data.value?.activityLevel?.averageWorkoutLength}")
        s.appendLine("WorkoutsPerWeek: ${data.value?.activityLevel?.workoutsPerWeek}")
        return s.toString()
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
            LocationServices.getFusedLocationProviderClient(context).getCurrentLocation(Priority.PRIORITY_LOW_POWER, null).addOnSuccessListener { newLocation ->
                if(newLocation != null) {
                    location = newLocation

                    val geocoder: Geocoder = Geocoder(context)
                    val addresses = geocoder.getFromLocation(newLocation.latitude, newLocation.longitude, 1)!!
                    if(addresses.size >= 1) {
                        locationName = addresses[0].let {
                            it.locality + ", " + it.adminArea + ", " + it.countryName
                        }
                        textLocation.city = addresses[0].locality
                        textLocation.state = addresses[0].adminArea
                        textLocation.country = addresses[0].countryName
                        textLocation.zipCode = addresses[0].postalCode
                        textLocation.streetAddress = addresses[0].thoroughfare
                    }


                    successCallback(newLocation)
                } else
                    Toast.makeText(context, "Couldn't find your location!", Toast.LENGTH_LONG).show()
            }
        } catch(e : SecurityException){}
        catch(e: java.io.IOException) {}
    }

    class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}