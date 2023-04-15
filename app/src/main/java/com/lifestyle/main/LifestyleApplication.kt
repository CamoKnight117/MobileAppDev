package com.lifestyle.main

import android.app.Application
import androidx.room.Database
import com.lifestyle.database.LifestyleDatabase
import com.lifestyle.user.UserRepository
import com.lifestyle.weather.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class LifestyleApplication : Application() {
    //global scope for coroutines
    val applicationScope = CoroutineScope(SupervisorJob())

    //Inject scope and application context into database
    val database by lazy{ LifestyleDatabase.getDatabase(this, applicationScope)}

    val userRepository by lazy{ UserRepository.getInstance(database.userDao(), applicationScope)}
    val weatherRepository by lazy{ WeatherRepository.getInstance(database.weatherDao(), applicationScope)}
}