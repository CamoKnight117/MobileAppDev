package com.lifestyle.main

import kotlinx.serialization.Serializable

@Serializable
public enum class Sex() { MALE, FEMALE, UNASSIGNED }
@Serializable
public enum class LastUsedModule() { MAIN, PROFILE, BMR, WEATHER, HIKES }