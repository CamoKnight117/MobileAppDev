package com.lifestyle.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lifestyle.R
import com.lifestyle.main.User
import com.lifestyle.main.UserProvider

/**
 * A placeholder activity for testing *ProfileFragment*.
 */
class ProfileActivity : AppCompatActivity(), UserProvider {
    private var lifestyleUser: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    override fun getUser(): User {
        return lifestyleUser
    }

}