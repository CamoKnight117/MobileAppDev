package com.lifestyle.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A response from https://api.openweathermap.org/data/2.5/weather, minus "internal parameter"s, parsed from JSON.
 * See https://openweathermap.org/current#parameter for details.
 */
@Serializable
class WeatherData (
    val coord: Coord? = null,
    val weather: List<Weather>? = null,
    val main: Main? = null,
    val visibility: Double? = null,
    val wind: Wind? = null,
    val rain: Rain? = null,
    val snow: Snow? = null,
    val dt: Double? = null,
    val sys: Sys? = null,
    val timezone: Double? = null
) {
    @Serializable
    public class Coord(val lon: Double? = null, val lat: Double? = null) { }
    @Serializable
    public class Weather(val id: Double? = null, val main: String? = null, val description: String? = null, val icon: String? = null) { }
    @Serializable
    public class Main(val temp: Double? = null, val feels_like: Double? = null, val temp_min: Double? = null, val temp_max: Double? = null, val pressure: Double? = null, val humidity: Double? = null, val sea_level: Double? = null, val grnd_level: Double? = null)
    @Serializable
    public class Wind(val speed: Double? = null, val deg: Double? = null, val gust: Double? = null) { }
    @Serializable
    public class Rain(@SerialName("1h") val oneH: Double? = null, @SerialName("3h") val threeH: Double? = null) { }
    @Serializable
    public class Snow(@SerialName("1h") val oneH: Double? = null, @SerialName("3h") val threeH: Double? = null) { }
    @Serializable
    public class Sys(val country: String? = null, val sunrise: Double? = null, val sunset: Double? = null) { }
}