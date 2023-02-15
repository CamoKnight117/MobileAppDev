package com.lifestyle.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.bmr.BMRPage
import com.lifestyle.profile.ProfileFragment

/*
    This is the heart of our mobile application
    We should structure it in a way that allows
    for scalability. This is more than just the
    consideration of multiple users and traffic
    . This involves computer security on three
    layers, a transport,



    This activity could be stored in a single table database design
 */
class MainActivity : AppCompatActivity(), UserProvider {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.prof_button).setOnClickListener {
            startFragment(ProfileFragment())
        }

        findViewById<Button>(R.id.button_bmr).setOnClickListener {
            startFragment(BMRPage())
        }
    }

    private fun startFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_view, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun getUser(): User {
        val user = User()
        user.age = 23
        user.height = 180
        user.weight = 70
        user.sex = User.Sex.MALE
        user.activityLevel.caloriesPerHour = 210
        user.activityLevel.workoutsPerWeek = 3
        user.activityLevel.averageWorkoutLength = 0.5f
        return user
    }
}