package com.lifestyle.user

import android.graphics.Bitmap
import android.location.Location
import com.lifestyle.bmr.ActivityLevel
import com.lifestyle.main.SerializableLocation
import com.lifestyle.map.TextLocation
import java.util.UUID

class UserData (
    private val uuid : UUID,
    var name: String? = null,
    var age: Int? = 0,
    private var serializableLocation: SerializableLocation? = null,
    var locationName: String? = null,
    var textLocation: TextLocation? = null,
    var height: Float? = 0.0f,
    var weight: Float? = 0.0f,
    var sex: Sex? = null,
    var activityLevel: ActivityLevel? = null,
    var lastUsedModule: LastUsedModule? = null,
    var profilePictureThumbnail: Bitmap? = null,
) {
    public var location: Location?
        get() = serializableLocation?.location
        set(value) {
            serializableLocation = if(value == null)
                null;
            else
                SerializableLocation(value)
        }
}