package com.lifestyle.main

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.lifestyleapp_spring2023.R
import com.lifestyle.bmr.BMRPage
import com.lifestyle.fragment.NavBar

/*
    This is the heart of our mobile application
    We should structure it in a way that allows
    for scalability. This is more than just the
    consideration of multiple users and traffic
    . This involves computer security on three
    layers, a transport,



    This activity could be stored in a single table database design
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun onButtonClicked(buttonId: Int) {
            // Handle button click here
        }

        butt.setOnClickListener {
            startFragment(NavBar())
        }

        bmr_button.setOnClickListener {
            startFragment(BMRPage())
        }
    }

    private fun startFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}