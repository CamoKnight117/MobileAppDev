package com.lifestyle.main

import com.lifestyle.bmr.ActivityLevel

class User() {
    var name = "Name";
    var age = 0;
    var location = "TODO";
    var height = 0;
    var weight = 0;
    var sex = Sex.UNASSIGNED;
    var activityLevel = "TODO";
    var profilePicture = "TODO";
}

enum class Sex
{
    MALE, FEMALE, UNASSIGNED
}

fun getLevel(): ActivityLevel.Level {
    //TODO: Implement this method. Return level based on activity level
    return ActivityLevel.Level.ACTIVE
}

fun getWeeklyCalorieIntake() {
    //TODO: Implement this method.
}

fun getDailyCalorieIntake() {
    //TODO: Implement this method.
}

fun caluclateBMR() {
    //TODO: Implement this method.
}