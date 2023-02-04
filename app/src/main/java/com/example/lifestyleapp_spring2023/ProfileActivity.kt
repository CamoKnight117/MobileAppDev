package com.example.lifestyleapp_spring2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmractivity)
    }

    protected override fun onSaveInstanceState(outState : Bundle) {
        super.onSaveInstanceState(outState)

    }
}