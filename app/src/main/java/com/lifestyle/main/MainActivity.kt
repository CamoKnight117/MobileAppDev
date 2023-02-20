package com.lifestyle.main

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.bmr.BMRPage
import com.lifestyle.fragment.NavBar
import com.lifestyle.profile.ProfileFragment
import com.lifestyle.weather.WeatherFragment

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
    private var user: User = User()
    init {
        user.age = 23
        user.height = 180
        user.weight = 70
        user.sex = User.Sex.MALE
        user.activityLevel.caloriesPerHour = 210
        user.activityLevel.workoutsPerWeek = 3
        user.activityLevel.averageWorkoutLength = 0.5f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load any saved [User] from storage.
        user = User.loadFromDevice(this) ?: user

        startFragment(NavBar())

        findViewById<Button>(R.id.prof_button).setOnClickListener {
            startFragment(ProfileFragment())
        }

        findViewById<Button>(R.id.button_bmr).setOnClickListener {
            startFragment(BMRPage())
        }

        findViewById<View>(R.id.weather_button).setOnClickListener {
            startFragment(WeatherFragment())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        user?.saveToDevice(this)
    }

    private fun startFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.button_card, fragment)
        fragmentTransaction.commit()
    }

    override fun getUser(): User {
        return user
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionRequestCodeToResultCallback[requestCode]?.invoke(this, permissions, grantResults)
    }

    companion object {
        private var permissionRequestCodeToResultCallback = HashMap<Int, PermissionRequestCallback>()
        private var nextPermissionRequestCode = 0

        public interface PermissionRequestCallback {
            fun invoke(activity: Activity, permissions: Array<out String>, grantResults: IntArray);
        }

        /**
         * @return The request code to pass to ActivityCompat.requestPermissions for permission requests that should invoke the given callback when they're completed.
         */
        public fun registerPermissionRequestCallback(callback : PermissionRequestCallback) : Int {
            val newPermissionRequestCode = nextPermissionRequestCode++
            permissionRequestCodeToResultCallback[newPermissionRequestCode] = callback
            return newPermissionRequestCode
        }
    }
}