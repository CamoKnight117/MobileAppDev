package com.lifestyle.main

import android.graphics.Bitmap
import android.location.Location
import com.lifestyle.bmr.ActivityLevel
import com.lifestyle.map.TextLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

class UserData (
    val name: String? = null,
    val age: Int? = 0,
    val location: Location? = null,
    val locationName: String? = null,
    val textLocation: TextLocation? = null,
    val height: Float? = 0.0f,
    val weight: Float? = 0.0f,
    val sex: Sex? = null,
    val activityLevel: ActivityLevel? = null,
    val lastUsedModule: LastUsedModule? = null,
    val profilePictureThumbnail: Bitmap? = null,
    val uuid : UUID? = null
) {
}