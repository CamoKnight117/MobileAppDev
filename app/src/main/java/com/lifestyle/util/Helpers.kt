package com.lifestyle.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.lifestyle.R
import com.lifestyle.bmr.Level
import com.lifestyle.main.User
import kotlin.math.roundToInt

class Helpers {
    companion object {
        fun setUpSpinner(context: Context, spinner: Spinner, items: Array<String>, isBold: Boolean) {
            var spinnerList = if (isBold) { R.layout.spinner_list_bold} else { R.layout.spinner_list}
            val arrayAdapter = ArrayAdapter<Any?>(context, spinnerList, items)
            arrayAdapter.setDropDownViewResource(spinnerList)
            spinner.adapter = arrayAdapter
        }

        fun updateNavBar(mainActivity: ComponentActivity, user: User) {
            mainActivity.findViewById<TextView>(R.id.recommendedCalorieIntakeValue).text =
                mainActivity.getString(R.string.calPerDayShort, user.getDailyCalorieIntake().roundToInt().toString())
            mainActivity.findViewById<TextView>(R.id.ageAndSexValue).text = mainActivity.getString(R.string.ageAndSex, user.age.toString(),  user.sex.toString().substring(0, 1))
            val activityLevel = when(user.activityLevel.getLevel())
            {
                Level.SEDENTARY -> "Sedentary"
                Level.LIGHTLY_ACTIVE -> "Lightly Active"
                Level.ACTIVE -> "Active"
                Level.VERY_ACTIVE -> "Very Active"
            }
            mainActivity.findViewById<TextView>(R.id.nameTextValue).text = user.name
            mainActivity.findViewById<TextView>(R.id.activityLevelValue).text = activityLevel
            mainActivity.findViewById<ImageButton>(R.id.imageButton).setImageBitmap(user.profilePictureThumbnail)
        }
    }
}