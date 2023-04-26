package com.lifestyle.user

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lifestyle.database.UserDao
import com.lifestyle.main.MainActivity
import com.lifestyle.map.TextLocation
import com.lifestyle.user.UserData.Companion.convertToJson
import com.lifestyle.user.UserData.Companion.convertToUserObject
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository(userDao: UserDao) {
    // Live data object notified when we've gotten the location
    val data : MutableLiveData<UserData> = MutableLiveData<UserData>()
    val userDao = userDao
    var addresses: List<Address>? = null

    fun setName(name : String) {
        data.value?.let {
            it.name = name
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setAge(age : Int) {
        data.value?.let {
            it.age = age
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setHeight(height : Float) {
        data.value?.let {
            it.height = height
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setWeight(weight : Float) {
        data.value?.let {
            it.weight = weight
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setSex(sex : Sex) {
        data.value?.let {
            it.sex = sex
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setTextLocation(location: TextLocation?) {
        data.value?.let {
            it.textLocation = location
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setLocationName(locationName: String) {
        data.value?.let {
            it.locationName = locationName
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setLocationDirect(location: Location) {
        data.value?.let {
            it.location = location
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setLastUsedModule(lastUsedModule: LastUsedModule) {
        data.value?.let {
            it.lastUsedModule = lastUsedModule
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    fun setProfilePictureThumbnail(thumbnail : Bitmap) {
        data.value?.let {
            it.profilePictureThumbnail = thumbnail
            data.postValue(it)
        }
        updateUserData(data.value)
    }

    /*fun updateLocation(activity: Activity) {
        mScope.launch(Dispatchers.IO) {
            if(trySecureLocationPermission(activity)) {
                val newLocation = fetchLocation(activity)

                if(newLocation != null) {
                    data.value?.let {
                        it.location = newLocation

                        val geocoder: Geocoder = Geocoder(activity)
                        val addresses = geocoder.getFromLocation(newLocation.latitude, newLocation.longitude, 1)!!
                        if(addresses.size >= 1) {
                            it.locationName = addresses[0].let {
                                it.locality + ", " + it.adminArea + ", " + it.countryName
                            }
                            it.textLocation?.city = addresses[0].let {
                                it.locality
                            }
                            it.textLocation?.state = addresses[0].let {
                                it.adminArea
                            }
                            it.textLocation?.country = addresses[0].let {
                                it.countryName
                            }
                            it.textLocation?.zipCode = addresses[0].let {
                                it.postalCode
                            }
                            it.textLocation?.streetAddress = addresses[0].let {
                                it.thoroughfare
                            }
                        }
                        data.postValue(it)
                    }
                } else
                    Toast.makeText(activity, "Couldn't find your location!", Toast.LENGTH_LONG).show()
            }
        }
        updateUserData(data.value)
    }*/

    fun updateLocation(activity: Activity) {
        mScope.launch(Dispatchers.IO) {
            if(trySecureLocationPermission(activity)) {
                val newLocation = fetchLocation(activity)

                if(newLocation != null) {
                    var newUserData = data.value
                    newUserData?.apply {
                        location = newLocation

                        val geocoder: Geocoder = Geocoder(activity)
                        val addresses = geocoder.getFromLocation(newLocation.latitude, newLocation.longitude, 1)!!
                        if(addresses.size >= 1) {
                            locationName = addresses[0].let {
                                it.locality + ", " + it.adminArea + ", " + it.countryName
                            }
                            textLocation?.city = addresses[0].let {
                                it.locality
                            }
                            textLocation?.state = addresses[0].let {
                                it.adminArea
                            }
                            textLocation?.country = addresses[0].let {
                                it.countryName
                            }
                            textLocation?.zipCode = addresses[0].let {
                                it.postalCode
                            }
                            textLocation?.streetAddress = addresses[0].let {
                                it.thoroughfare
                            }
                        }

                        data.postValue(this)
                    }
                } else
                    Toast.makeText(activity, "Couldn't find your location!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getLocationFromGeocoder(activity: Activity, stringLocation: String) = runBlocking {
        launch {
            try {
                val geocoder = Geocoder(activity)
                addresses = geocoder.getFromLocationName(stringLocation, 1)!!
            }
            catch (e: java.io.IOException)
            {
                //This occurs if there are no text inputs. Just do nothing in this case and carry on
            }
        }
    }

    fun setLocationFromText(activity: Activity, stringLocation: String) {
        getLocationFromGeocoder(activity, stringLocation)
        if(addresses != null && addresses!!.size != 0)
        {
            data.value?.let {
                if (addresses!!.size >= 1) {
                    it.locationName = addresses!![0].let {
                        it.locality + ", " + it.adminArea + ", " + it.countryName
                    }
                    it.textLocation = TextLocation()
                    it.textLocation?.city = addresses!![0].let {
                        it.locality
                    }
                    it.textLocation?.state = addresses!![0].let {
                        it.adminArea
                    }
                    it.textLocation?.country = addresses!![0].let {
                        it.countryName
                    }
                    it.textLocation?.zipCode = addresses!![0].let {
                        it.postalCode
                    }
                    it.textLocation?.streetAddress = addresses!![0].let {
                        it.thoroughfare
                    }
                    it.location = Location(it.locationName) //Attempt to set location this way
                    it.location?.latitude = addresses!![0].latitude
                    it.location?.longitude = addresses!![0].longitude
                }
                data.postValue(it)
            }
            updateUserData(data.value)
        }
    }

    fun fetchUserData(id: String) = runBlocking {
        launch {
            val row = userDao.getUser(id)
            val obj = convertToUserObject(row)
            if (obj != null) {
                data.postValue(obj!!)
            }
        }
    }

    fun updateUserData(userData: UserData?) = runBlocking {
        launch {
            if (userData != null) {
                val userTable = convertToJson(userData)
                userDao.insert(userTable)
            }
        }
    }

    @WorkerThread
    private suspend fun trySecureLocationPermission(activity: Activity) = suspendCoroutine<Boolean> { continuation ->
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestedPermission = Manifest.permission.ACCESS_COARSE_LOCATION
            val requestCode = MainActivity.registerPermissionRequestCallback(object :
                MainActivity.Companion.PermissionRequestCallback {
                override fun invoke(
                    activity: Activity,
                    permissions: Array<out String>,
                    grantResults: IntArray
                ) {
                    if(grantResults[permissions.indexOf(requestedPermission)] == PackageManager.PERMISSION_GRANTED)
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }
            })
            // Calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(activity, arrayOf(requestedPermission), requestCode)
        } else
            continuation.resume(true)
    }

    @WorkerThread
    private suspend fun fetchLocation(activity: Activity) = suspendCoroutine<Location?> { continuation ->
        try {
            LocationServices.getFusedLocationProviderClient(activity).getCurrentLocation(
                Priority.PRIORITY_LOW_POWER, null).addOnSuccessListener { newLocation ->
                    continuation.resume(newLocation)
                }
        } catch(e : SecurityException){
            e.printStackTrace()
            continuation.resume(null)
        }
        catch(e: java.io.IOException) {
            e.printStackTrace()
            continuation.resume(null)
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

}