package com.lifestyle.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.lifestyle.R

class Helpers {
    companion object {
        fun setUpSpinner(context: Context, spinner: Spinner, items: Array<String>) {
            val arrayAdapter = ArrayAdapter<Any?>(context, R.layout.spinner_list, items)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
            spinner.adapter = arrayAdapter
        }
    }
}