package com.lifestyle.database

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.lifestyle.weather.WeatherData

@Entity(tableName = "weather_table")
data class WeatherTable(
    @field:ColumnInfo(name = "coordinate")
    @field:PrimaryKey
    var coord: WeatherData.Coord,
    @field:ColumnInfo(name = "weatherdata")
    var weatherJson: String
)
