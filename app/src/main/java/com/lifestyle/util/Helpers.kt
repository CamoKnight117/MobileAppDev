package com.lifestyle.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.lifestyle.R

class Helpers {
    companion object {
        fun setUpSpinner(context: Context, spinner: Spinner, items: Array<String>, isBold: Boolean) {
            var spinnerList = if (isBold) { R.layout.spinner_list_bold} else { R.layout.spinner_list}
            val arrayAdapter = ArrayAdapter<Any?>(context, spinnerList, items)
            arrayAdapter.setDropDownViewResource(spinnerList)
            spinner.adapter = arrayAdapter
        }
    }
}