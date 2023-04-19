package com.lifestyle.weather

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModel(repository: WeatherRepository) : ViewModel() {
    private val weatherData : LiveData<WeatherData> = repository.data
    private var weatherRepository : WeatherRepository = repository

    val data: LiveData<WeatherData>
        get() = weatherData

    public fun setLocation(location: Location) {
        weatherRepository.setLocation(location)
    }
}

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}