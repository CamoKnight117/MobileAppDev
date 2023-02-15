package com.lifestyle.main

import android.graphics.Bitmap
import com.lifestyle.bmr.ActivityLevel

class User() {
    var name: String? = null;
    var age = 0;
    var location = "TODO";
    var height = 0;
    var weight = 0;
    var sex = Sex.UNASSIGNED;
    var activityLevel = ActivityLevel();
    var profilePicture = "TODO";
    var profilePictureThumbnail : Bitmap? = null

    enum class Sex
    {
        MALE, FEMALE, UNASSIGNED
    }

    fun getDailyCalorieIntake() : Float {
        val bmrVal = calculateBMR()
        return if(bmrVal != 0f) {
            activityLevel.workoutCaloriesPerWeek()/7 + bmrVal;
        } else {
            0f;
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
}

