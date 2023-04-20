package com.lifestyle.user

import kotlinx.serialization.Serializable

@Serializable
enum class Sex { MALE, FEMALE, UNASSIGNED }
@Serializable
enum class LastUsedModule { MAIN, PROFILE, BMR, WEATHER, HIKES }