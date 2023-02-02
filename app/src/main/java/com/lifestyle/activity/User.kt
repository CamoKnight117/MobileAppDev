package com.lifestyle.activity

class User {
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