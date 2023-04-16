package com.lifestyle.database

import androidx.room.TypeConverter
import com.lifestyle.weather.WeatherData

class Converters {
    @TypeConverter
    fun coordToString(coord: WeatherData.Coord?): String? {
        return coord?.lon?.toString()?.plus(", ")?.plus(coord.lat)
    }

    @TypeConverter
    fun stringToCoord(string: String?): WeatherData.Coord? {
        if (string == null) {
            return null
        } else {
            val split = string.split(", ")
            return WeatherData.Coord(split[0].toDouble(), split[1].toDouble())
        }
    }
}