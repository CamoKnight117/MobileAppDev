package com.lifestyle.main

import android.graphics.Bitmap
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lifestyle.bmr.ActivityLevel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.FileNotFoundException
import com.lifestyle.map.TextLocation

@Serializable
class User() {
    var name: String? = null
    var age = 0
    public var location: Location?
        get() = serializableLocation?.location
        set(value) {
            serializableLocation = if(value == null)
                null;
            else
                SerializableLocation(value)
        }
    private var serializableLocation: SerializableLocation? = null
    var locationName: String? = null
    var textLocation = TextLocation()
    
    var height = 0.0f
        get() {
            return field / 2.54f
        }
        set(value) {
            //1 in = 2.54 cm, and we store the height as centimeters (for easier calculation for BMR)
            field = value * 2.54f
        }
    var weight = 0.0f
        get() {
            return field / 0.453592f
        }
        set(value) {
            //1 lb = 0.453592 kg, and we store the weight as kilograms (for easier calculation for BMR)
            field = value * 0.453592f
        }
    var sex = Sex.UNASSIGNED
    var activityLevel = ActivityLevel()
    var profilePicture = "TODO"
    @Transient
    var profilePictureThumbnail : Bitmap? = null

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
        return if(age != 0 && height != 0.0f && weight != 0.0f && sex != Sex.UNASSIGNED) {
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

    /** Write this [User] to a reserved location in internal storage. */
    public fun saveToDevice(context: Context) {
        // Save user data.
        context.openFileOutput(userSavePath, Context.MODE_PRIVATE).use { file ->
            file.write(Json.encodeToString(this).encodeToByteArray());
        }
        // Save the profile picture thumbnail.
        val portrait = profilePictureThumbnail
        if(portrait != null) {
            context.openFileOutput(userThumbnailSavePath, Context.MODE_PRIVATE).use { file ->
                portrait.compress(Bitmap.CompressFormat.PNG, 90, file)
            }
        }
    }

    companion object {
        private val userSavePath = "userProfile.json"
        private val userThumbnailSavePath = "userProfileThumbnail.png"

        /** @return The User stored in internal storage, or null if no user is stored. */
        public fun loadFromDevice(context: Context): User? {
            var result: User? = null
            try {
                // Read user data.
                context.openFileInput(userSavePath).use { file ->
                    result = Json{ignoreUnknownKeys=true}.decodeFromString<User>(file.readBytes().decodeToString())
                }
                // Read the profile picture thumbnail.
                context.openFileInput(userThumbnailSavePath).use { file ->
                    result?.profilePictureThumbnail = BitmapFactory.decodeStream(file)
                }
            } catch (e: FileNotFoundException) { }
            return result
        }
    }
}


