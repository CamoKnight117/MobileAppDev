package com.lifestyle.main

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    // Live data object notified when we've gotten the location
    public val data : MutableLiveData<UserData> = MutableLiveData<UserData>()

    public fun update() {
        //TODO: put update code here
    }

    companion object {

        @Volatile
        private var instance : UserRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        public fun getInstance(application: Application) : UserRepository {
            if(instance==null)
                instance = UserRepository(application)
            return instance!!
        }
    }

    inner class FetchLocationTask(var context : Context) {
        var executorService = Executors.newSingleThreadExecutor()
        var mainThreadHandler : Handler = HandlerCompat.createAsync(Looper.getMainLooper())

        public fun execute(location: Location) {
            executorService.execute {
                try {
                    LocationServices.getFusedLocationProviderClient(context).getCurrentLocation(
                        Priority.PRIORITY_LOW_POWER, null).addOnSuccessListener{ newLocation -> postToMainThread(newLocation) }
                } catch(e : SecurityException){
                    e.printStackTrace()
                }
                catch(e: java.io.IOException) {
                    e.printStackTrace()
                }
            }
        }

        private fun postToMainThread(newLocation: Location) {
            mainThreadHandler.post {
                if(newLocation != null) {
                    data.value?.apply {
                        location = newLocation

                        val geocoder: Geocoder = Geocoder(context)
                        val addresses = geocoder.getFromLocation(newLocation.latitude, newLocation.longitude, 1)!!
                        if(addresses.size >= 1) {
                            locationName = addresses[0].let {
                                it.locality + ", " + it.adminArea + ", " + it.countryName
                            }
                            textLocation?.city = addresses[0].locality
                            textLocation?.state = addresses[0].adminArea
                            textLocation?.country = addresses[0].countryName
                            textLocation?.zipCode = addresses[0].postalCode
                            textLocation?.streetAddress = addresses[0].thoroughfare
                        }
                    }
                } else
                    Toast.makeText(context, "Couldn't find your location!", Toast.LENGTH_LONG).show()
            }
        }
    }


}