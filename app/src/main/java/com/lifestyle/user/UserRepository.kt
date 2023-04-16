package com.lifestyle.user

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lifestyle.bmr.ActivityLevel
import com.lifestyle.database.UserDao
import com.lifestyle.database.UserTable
import com.lifestyle.main.MainActivity
import com.lifestyle.main.SerializableLocation
import com.lifestyle.map.TextLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import java.util.concurrent.Executors

class UserRepository(userDao: UserDao) {
    // Live data object notified when we've gotten the location
    val data : MutableLiveData<UserData> = MutableLiveData<UserData>()
    val userDao = userDao

    fun update() {
        //TODO: put update code here
    }

    fun fetchUserData(name: String) = runBlocking {
        launch {
            val row = userDao.getUser(name)
            data.postValue(UserData(
                row.uuid,
                row.name,
                row.age,
                /*TODO: serializablelocation*/ null,
                /*TODO: locationname*/ null,
                /*TODO: textlocation*/ null,
                row.height,
                row.weight,
                row.sex,
                /*TODO: activityLevel*/ null,
                /*TODO: lastUsedModule*/ null,
                /*TODO: profilePictureThumbnail*/ null
            ))
        }
    }

    companion object {

        @Volatile
        private var mInstance : UserRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        fun getInstance(userDao: UserDao, scope: CoroutineScope) : UserRepository {
            mScope = scope
            return mInstance?: synchronized(this) {
                val instance = UserRepository(userDao)
                mInstance = instance
                instance
            }
        }
    }

    inner class FetchLocationTask(var activity : Activity) {
        var executorService = Executors.newSingleThreadExecutor()
        var mainThreadHandler : Handler = HandlerCompat.createAsync(Looper.getMainLooper())

        public fun execute(location: Location) {
            executorService.execute {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val requestCode = MainActivity.registerPermissionRequestCallback(object :
                        MainActivity.Companion.PermissionRequestCallback {
                        override fun invoke(
                            activity: Activity,
                            permissions: Array<out String>,
                            grantResults: IntArray
                        ) {
                            setLocationToLastDeviceLocation()
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
                } else
                    setLocationToLastDeviceLocation()
            }
        }

        private fun setLocationToLastDeviceLocation() {
            try {
                LocationServices.getFusedLocationProviderClient(activity).getCurrentLocation(
                    Priority.PRIORITY_LOW_POWER, null).addOnSuccessListener{ newLocation -> postToMainThread(newLocation) }
            } catch(e : SecurityException){
                e.printStackTrace()
            }
            catch(e: java.io.IOException) {
                e.printStackTrace()
            }
        }

        private fun postToMainThread(newLocation: Location) {
            mainThreadHandler.post {
                if(newLocation != null) {
                    data.value?.apply {
                        location = newLocation

                        val geocoder: Geocoder = Geocoder(activity)
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
                    Toast.makeText(activity, "Couldn't find your location!", Toast.LENGTH_LONG).show()
            }
        }
    }


}