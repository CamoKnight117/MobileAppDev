package com.lifestyle.user

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import com.lifestyle.bmr.ActivityLevel
import com.lifestyle.main.SerializableLocation
import com.lifestyle.map.TextLocation
import java.io.ByteArrayOutputStream
import java.util.UUID
import android.graphics.Bitmap.CompressFormat

import android.R.attr.bitmap
import com.lifestyle.database.UserTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
class UserData (
    @Transient
    var uuid : UUID? = null,
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
    @Transient
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

    companion object {
        fun convertToUserObject(table: UserTable): UserData?
        {
            val userJson = table.userJson
            val profilePic: ByteArray? = table.profilePic
            var result: UserData? = null
            try {
                result = Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<UserData>(userJson)
            } catch (ignore: Exception) {}
            if (profilePic != null && result != null) {
                val bitmap = BitmapFactory.decodeByteArray(profilePic, 0, profilePic.size)
                result.profilePictureThumbnail = bitmap
            }

            return result
        }

        fun convertToJson(userData: UserData): UserTable
        {
            val tempProfileThumbnail = userData.profilePictureThumbnail
            userData.profilePictureThumbnail = null;
            val userJson = Json.encodeToString(userData)

            val blob = ByteArrayOutputStream()
            var bitmapdata: ByteArray? = null
            if (tempProfileThumbnail != null) {
                tempProfileThumbnail.compress(CompressFormat.PNG, 0, blob)
                bitmapdata = blob.toByteArray()
            }

            userData.profilePictureThumbnail = tempProfileThumbnail
            
            val uuid = userData.uuid

            return UserTable(uuid!!, userJson, bitmapdata)
        }
    }
}