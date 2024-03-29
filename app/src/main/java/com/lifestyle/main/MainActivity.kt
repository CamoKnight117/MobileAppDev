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
import androidx.lifecycle.Observer
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
class MainActivity : AppCompatActivity(), fragmentStarterInterface {

    private val mUserViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((application as LifestyleApplication).userRepository)
    }

    private var isFirstTime = true

    //create an observer that watches the LiveData<UserData> object
    private val liveDataObserver: Observer<UserData> =
        Observer { userData -> // Update the UI if this data variable changes
            Helpers.updateNavBar(this, mUserViewModel)
            if (isFirstTime) {
                isFirstTime = false
                when (userData.lastUsedModule) {
                    LastUsedModule.PROFILE -> startProfileFrag()
                    LastUsedModule.BMR -> startBMRFrag()
                    LastUsedModule.WEATHER -> startWeatherFrag()
                    LastUsedModule.HIKES -> startHikesFrag()
                    else -> startMainFrag()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mUserViewModel.data.observe(this, liveDataObserver)

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
        interface PermissionRequestCallback {
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
        if (mUserViewModel.data.value != null) {
            mUserViewModel.setLastUsedModule(LastUsedModule.PROFILE)
        }
        startFragment(ProfileFragment())
    }

    override fun startWeatherFrag() {
        setVisibility(true)
        if (mUserViewModel.data.value != null) {
            mUserViewModel.setLastUsedModule(LastUsedModule.WEATHER)
        }
        startFragment(WeatherFragment())
    }

    override fun startBMRFrag() {
        setVisibility(true)
        if (mUserViewModel.data.value != null) {
            mUserViewModel.setLastUsedModule(LastUsedModule.BMR)
        }
        startFragment(BMRPage())
    }

    override fun startHikesFrag() {
        setVisibility(true)
        if (mUserViewModel.data.value != null) {
            mUserViewModel.setLastUsedModule(LastUsedModule.HIKES)
        }
        startFragment(MapFragment())
    }

    override fun startMainFrag() {
        setVisibility(false)
        if (mUserViewModel.data.value != null) {
            mUserViewModel.setLastUsedModule(LastUsedModule.MAIN)
        }
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