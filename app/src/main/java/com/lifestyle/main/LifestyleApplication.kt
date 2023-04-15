package com.lifestyle.main

import android.app.Application
import com.lifestyle.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class LifestyleApplication : Application() {
    //global scope for coroutines
    val applicationScope = CoroutineScope(SupervisorJob())

    //Inject scope and application context into database
    val database by lazy{WeatherDatabase.getDatabase(this, applicationScope)}

    //TODO: Inject scope and application into User database

    //TODO: refactor to be similar to example 35
    val repository by lazy{ UserRepository.getInstance(database.weatherDao())}

}