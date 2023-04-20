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
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.os.HandlerCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lifestyle.database.UserDao
import com.lifestyle.main.MainActivity
import com.lifestyle.user.UserData.Companion.convertToJson
import com.lifestyle.user.UserData.Companion.convertToUserObject
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository(userDao: UserDao) {
    // Live data object notified when we've gotten the location
    val data : MutableLiveData<UserData> = MutableLiveData<UserData>()
    val userDao = userDao

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

    fun update(activity: Activity) {
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
                            textLocation?.city = addresses[0].locality
                            textLocation?.state = addresses[0].adminArea
                            textLocation?.country = addresses[0].countryName
                            textLocation?.zipCode = addresses[0].postalCode
                            textLocation?.streetAddress = addresses[0].thoroughfare
                        }

                        data.postValue(this)
                    }
                } else
                    Toast.makeText(activity, "Couldn't find your location!", Toast.LENGTH_LONG).show()
            }
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

    inner class FetchLocationTask(var activity : Activity) {
        var executorService = Executors.newSingleThreadExecutor()
        var mainThreadHandler : Handler = HandlerCompat.createAsync(Looper.getMainLooper())

        public fun execute(location: Location) {
        }




    }


}