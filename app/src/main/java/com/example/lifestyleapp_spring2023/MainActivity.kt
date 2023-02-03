package com.example.lifestyleapp_spring2023

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val redButton: Button = findViewById(R.id.prof_button)
        redButton.setBackgroundColor(Color.RED)

        val orangeButton: Button = findViewById(R.id.bmr_button)
        orangeButton.setBackgroundColor(Color.rgb(255, 165, 0))

        val yellowButton: Button = findViewById(R.id.weather_button)
        yellowButton.setBackgroundColor(Color.YELLOW)

        val greenButton: Button = findViewById(R.id.hikes_button)
        greenButton.setBackgroundColor(Color.GREEN)

        //Find each frame layout, replace with corresponding fragment
        val fTrans: FragmentTransaction = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.bmrCard, BMRPage(), "Frag_1")
        fTrans.replace(R.id.bmrDataCard, NavBar(), "Frag_2")
        fTrans.commit()
    }
}