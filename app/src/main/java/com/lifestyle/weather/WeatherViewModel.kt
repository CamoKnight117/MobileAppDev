package com.lifestyle.weather

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel(repository: WeatherRepository) : ViewModel() {
    private val weatherData : LiveData<WeatherData> = repository.data
    private var weatherRepository : WeatherRepository = repository

    val data: LiveData<WeatherData>
        get() = weatherData

    public fun setLocation(location: Location) {
        weatherRepository.setLocation(location)
    }
}