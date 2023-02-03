package com.lifestyle.bmr

class ActivityLevel() {
    var workoutsPerWeek: Int = 0
    var averageWorkoutLength: Float = 0f
    var caloriesPerHour: Int = 0
    var intensity: Intensity = Intensity.NONE

    fun getCaloriesBurnedPerWorkout(): Float {
        return averageWorkoutLength * caloriesPerHour;
    }

    fun workoutCaloriesPerWeek(): Float {
        return getCaloriesBurnedPerWorkout() * workoutsPerWeek;
    }

    enum class Intensity {
        NONE, MILD, MODERATE, INTENSE
    }

    enum class Level {
        SEDENTARY, LIGHTLY_ACTIVE, ACTIVE, VERY_ACTIVE
    }
}


