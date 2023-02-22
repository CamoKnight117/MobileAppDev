package com.lifestyle.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lifestyle.R
import com.lifestyle.main.User
import com.lifestyle.main.UserProvider

class WeatherActivity : AppCompatActivity(), UserProvider {
    private var lifestyleUser: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
    }

    override fun getUser(): User {
        return lifestyleUser
    }
}