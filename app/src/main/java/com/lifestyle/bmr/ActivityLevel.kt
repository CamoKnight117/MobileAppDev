package com.lifestyle.bmr

class ActivityLevel() {
    var workoutsPerWeek: Int = 0
    var averageWorkoutLength: Float = 0f
    var caloriesPerHour: Int = 0
        set(value) {
            field = value
            intensity = if(value < 300)
                Intensity.MILD;
            else if(value < 600)
                Intensity.MODERATE;
            else
                Intensity.INTENSE;
        }
    var intensity: Intensity = Intensity.NONE
        set(value) {
            field = value;
            caloriesPerHour = when(value) {
                Intensity.MILD -> 150
                Intensity.MODERATE -> 450
                Intensity.INTENSE -> 750
                Intensity.NONE -> 0
            }
        }

    fun getCaloriesBurnedPerWorkout(): Float {
        return averageWorkoutLength * caloriesPerHour;
    }

    fun workoutCaloriesPerWeek(): Float {
        return getCaloriesBurnedPerWorkout() * workoutsPerWeek;
    }
    fun getLevel(): Level {
        //Sedentary : 0 - 300
        //Lightly Active : 301 - 900
        //Active : 901 - 1500
        //Very Active : 1501+
        return if(workoutCaloriesPerWeek() < 301)
            Level.SEDENTARY
        else if(workoutCaloriesPerWeek() < 901)
            Level.LIGHTLY_ACTIVE
        else if(workoutCaloriesPerWeek() < 1501)
            Level.ACTIVE
        else
            Level.VERY_ACTIVE
    }

    enum class Intensity {
        NONE, MILD, MODERATE, INTENSE
    }

    enum class Level {
        SEDENTARY, LIGHTLY_ACTIVE, ACTIVE, VERY_ACTIVE
    }



}


