package com.lifestyle.bmr

import android.content.Context
import com.lifestyle.R

@kotlinx.serialization.Serializable
class ActivityLevel {
    var workoutsPerWeek: Int = 0
    var averageWorkoutLength: Int = 0
    var caloriesPerHour: Int = 0
        set(value) {
            field = value
            intensity = if (value < 300)
                Intensity.MILD
            else if (value < 600)
                Intensity.MODERATE
            else
                Intensity.INTENSE
        }
    private var intensity: Intensity = Intensity.NONE

    fun getCaloriesBurnedPerWorkout(): Float {
        return averageWorkoutLength * caloriesPerHour / 60.0f
    }

    fun workoutCaloriesPerWeek(): Float {
        return getCaloriesBurnedPerWorkout() * workoutsPerWeek
    }

    fun getLevel(): Level {
        //Sedentary : 0 - 500
        //Lightly Active : 501 - 1500
        //Active : 1500 - 3000
        //Very Active : 3001+
        return if (workoutCaloriesPerWeek() < 501)
            Level.SEDENTARY
        else if (workoutCaloriesPerWeek() < 1501)
            Level.LIGHTLY_ACTIVE
        else if (workoutCaloriesPerWeek() < 3001)
            Level.ACTIVE
        else
            Level.VERY_ACTIVE
    }

    enum class Intensity {
        NONE, MILD, MODERATE, INTENSE
    }
}

enum class Level {
    SEDENTARY, LIGHTLY_ACTIVE, ACTIVE, VERY_ACTIVE;

    fun name(context: Context): String {
        return context.resources.getStringArray(R.array.activityLevel)[ordinal]
    }
}


