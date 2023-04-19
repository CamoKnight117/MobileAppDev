package com.lifestyle.main

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import com.lifestyle.R
import com.lifestyle.bmr.BMRPage
import com.lifestyle.map.MapFragment
import com.lifestyle.profile.ProfileFragment
import com.lifestyle.user.*
import com.lifestyle.util.Helpers
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
class MainActivity : AppCompatActivity(), UserProvider, fragmentStarterInterface {
    private val mUserViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((application as LifestyleApplication).userRepository)
    }
    private var user : UserData? = UserData("Bob Ross", 33)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //user = mUserViewModel.data.value



        val backButton = findViewById<ImageView>(R.id.back_button)

        backButton.setOnClickListener {
            startMainFrag()
        }

        findViewById<Button>(R.id.prof_main_button).setOnClickListener {
            startProfileFrag()
        }

        findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            startProfileFrag()
        }

        findViewById<Button>(R.id.bmr_main_button).setOnClickListener {
            startBMRFrag()
        }

        findViewById<View>(R.id.weather_main_button).setOnClickListener {
            startWeatherFrag()
        }
        
        findViewById<Button>(R.id.hikes_main_button).setOnClickListener {
            startHikesFrag()
        }

        Helpers.updateNavBar(this, mUserViewModel)
        when (user!!.lastUsedModule) {
            null, LastUsedModule.MAIN -> startMainFrag()
            LastUsedModule.PROFILE -> startProfileFrag()
            LastUsedModule.BMR -> startBMRFrag()
            LastUsedModule.WEATHER -> startWeatherFrag()
            LastUsedModule.HIKES -> startHikesFrag()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun startFragment(fragment: Fragment) {
        //Save any changes to user before switching fragments
        //user?.saveToDevice(this)
        //Set up fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_view, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun getUserViewModel(): UserViewModel {
        return mUserViewModel!!
    }

    override fun getUser(): UserData {
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

    override fun startProfileFrag() {
        setVisibility(true)
        user!!.lastUsedModule = LastUsedModule.PROFILE
        startFragment(ProfileFragment())
    }

    override fun startWeatherFrag() {
        setVisibility(true)
        user!!.lastUsedModule = LastUsedModule.WEATHER
        startFragment(WeatherFragment())
    }

    override fun startBMRFrag() {
        setVisibility(true)
        user!!.lastUsedModule = LastUsedModule.BMR
        startFragment(BMRPage())
    }

    override fun startHikesFrag() {
        setVisibility(true)
        user!!.lastUsedModule = LastUsedModule.HIKES
        startFragment(MapFragment())
    }

    override fun startMainFrag() {
        setVisibility(false)
        user!!.lastUsedModule = LastUsedModule.MAIN
        startFragment(MainFragment())
    }

    private fun setVisibility(visible:Boolean) {
        val navbar = findViewById<CardView>(R.id.bottom_navbar)
        val backButton = findViewById<ImageView>(R.id.back_button)
        if(visible)
        {
            navbar.visibility = View.VISIBLE
            backButton.visibility = View.VISIBLE
        }
        else
        {
            navbar.visibility = View.GONE
            backButton.visibility = View.GONE
        }
    }
}