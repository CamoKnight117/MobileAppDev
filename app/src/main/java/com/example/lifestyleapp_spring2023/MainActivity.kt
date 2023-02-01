package com.example.lifestyleapp_spring2023

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Find each frame layout, replace with corresponding fragment
        //Find each frame layout, replace with corresponding fragment
        val fTrans: FragmentTransaction = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.fragmentFrame1, bmr_page_fragment(), "Frag_1")
        fTrans.commit()
    }
}