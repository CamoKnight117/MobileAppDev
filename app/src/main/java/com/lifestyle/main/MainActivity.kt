package com.lifestyle.main

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.bmr.BMRPage
import com.lifestyle.bmr.Level
import com.lifestyle.map.MapFragment
import com.lifestyle.profile.ProfileFragment
import com.lifestyle.util.Helpers
import com.lifestyle.weather.WeatherFragment
import kotlin.math.roundToInt

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
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load any saved [User] from storage.
        user = User.loadFromDevice(this) ?: user

        if(user == null)
            initUser()

        findViewById<Button>(R.id.main_button).setOnClickListener {
            startFragment(MainFragment())
        }

        findViewById<Button>(R.id.prof_button).setOnClickListener {
            startFragment(ProfileFragment())
        }

        findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            startFragment(ProfileFragment())
        }

        findViewById<Button>(R.id.button_bmr).setOnClickListener {
            startFragment(BMRPage())
        }

        findViewById<View>(R.id.weather_button).setOnClickListener {
            startFragment(WeatherFragment())
        }
        
        findViewById<Button>(R.id.hikes_button).setOnClickListener {
            startFragment(MapFragment())
        }

        Helpers.updateNavBar(this, user!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        user?.saveToDevice(this)
    }
    

    private fun initUser() {
        user = User()
        user!!.name = "Bob Ross"
        user!!.age = 23
        user!!.height = 72.0f
        user!!.weight = 145.0f
        user!!.sex = User.Sex.MALE
        user!!.activityLevel.caloriesPerHour = 210
        user!!.activityLevel.workoutsPerWeek = 3
        user!!.activityLevel.averageWorkoutLength = 0.5f
    }

    private fun startFragment(fragment: Fragment) {
        //Save any changes to user before switching fragments
        user?.saveToDevice(this)
        //Set up fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_view, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun getUser(): User {
        return user!!
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionRequestCodeToResultCallback[requestCode]?.invoke(this, permissions, grantResults)
    }

    private val isTablet: Boolean
        get() = resources.getBoolean(R.bool.isTablet)

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