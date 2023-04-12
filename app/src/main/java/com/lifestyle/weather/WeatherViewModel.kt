package com.lifestyle.weather

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    public val data : LiveData<WeatherData>
        get() = repository.data
    private var repository : WeatherRepository

    init {
        repository = WeatherRepository.getInstance(application)
    }

    public fun setLocation(location: Location) {
        repository.setLocation(location)
    }
}