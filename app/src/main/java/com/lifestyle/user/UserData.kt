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




@Serializable
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

    companion object {
        public fun convertToUserObject(UserTable table)
        {
            val userJson = table.userJson
            val profilePic = table.profilePic
            var result: UserData = null
            try {
                result = Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<UserData>(userJson)
            } catch (ignore: Exception) {}
            Bitmap bitmap = BitmapFactory.decodeByteArray(profilePic, 0, profilePic.length)
            result.profilePictureThumbnail = bitmap

            return result
        }

        public fun convertToJson(UserData userData)
        {
            Bitmap tempProfileThumbnail = userData.profilePictureThumbnail;
            userData.profilePictureThumbnail = null;
            String userJson = Json.encodeToString(userData)

            var blob = ByteArrayOutputStream()
            tempProfileThumbnail.compress(CompressFormat.PNG, 0 /* Ignored for PNGs */, blob)
            var bitmapdata: ByteArray = blob.toByteArray()

            userData.profilePictureThumbnail = tempProfileThumbnail
            
            var uuid = userData.uuid

            return userTable(uuid, userJson, bitmapdata)
        }
    }
}