package com.lifestyle.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.lifestyleapp_spring2023.R
import com.lifestyle.bmr.BMRPage
import com.lifestyle.fragment.NavBar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Find each frame layout, replace with corresponding fragment
        val fTrans: FragmentTransaction = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.fragmentFrame1, BMRPage(), "Frag_1")
        fTrans.replace(R.id.fragmentFrame2, NavBar(), "Frag_2")
        fTrans.commit()
    }
}