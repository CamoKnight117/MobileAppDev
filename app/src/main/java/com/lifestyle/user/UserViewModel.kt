package com.lifestyle.user

import android.app.Activity
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.*
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.lifestyle.main.MainActivity
import java.util.*

class UserViewModel(repository: UserRepository) : ViewModel() {
    private val userData: LiveData<UserData> = repository.data
    private val userRepository : UserRepository = repository

    val data: LiveData<UserData>
        get() = userData

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
            when (data.value?.sex) {
                Sex.MALE -> 88.362f + (29.535f * data.value?.weight!!) + (1.889f*data.value?.height!!)-(5.677f*data.value?.age!!)
                Sex.FEMALE -> (447.593f + (20.386f * data.value?.weight!!) + (1.220f * data.value?.height!!)-(4.330f * data.value?.age!!))
                Sex.UNASSIGNED -> 0f
                null -> 0f
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

    fun setName(name : String) {
        userRepository.setName(name)
    }
    fun setAge(age : Int) {
        userRepository.setAge(age)
    }
    fun setHeight(height : Float) {
        userRepository.setHeight(height)
    }
    fun setWeight(weight : Float) {
        userRepository.setWeight(weight)
    }
    fun setSex(sex : Sex) {
        userRepository.setSex(sex)
    }
    fun setLastUsedModule(lastUsedModule: LastUsedModule) {
        userRepository.setLastUsedModule(lastUsedModule)
    }
    fun setProfilePictureThumbnail(thumbnail : Bitmap) {
        userRepository.setProfilePictureThumbnail(thumbnail)
    }

    /**
     * Sets this User's location to the device's most recent coarse location. Asks for coarse location permissions if needed.
     * @param activity The Activity context that should be responsible for the location permission and request.
     */
    fun update(activity: Activity) {
        userRepository.update(activity)
    }

    class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                repository.fetchUserData("0")
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}