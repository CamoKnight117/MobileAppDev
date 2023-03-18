package com.lifestyle.bmr

import android.content.Context
import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lifestyle.R
import com.lifestyle.main.MainActivity
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BMRFragmentTest {
    private val clickTopCentre =
        actionWithAssertions(
            GeneralClickAction(
                Tap.SINGLE,
                GeneralLocation.TOP_CENTER,
                Press.FINGER,
                InputDevice.SOURCE_UNKNOWN,
                MotionEvent.BUTTON_PRIMARY
            )
        )

    private val clickBottomCentre =
        actionWithAssertions(
            GeneralClickAction(
                Tap.SINGLE,
                GeneralLocation.BOTTOM_CENTER,
                Press.FINGER,
                InputDevice.SOURCE_UNKNOWN,
                MotionEvent.BUTTON_PRIMARY
            )
        )

    private fun onWorkoutLength() = onView(withId(R.id.workoutLengthValue))
    private fun onWorkoutsPerWeek() = onView(withId(R.id.workoutsPerWeekValue))
    private fun onCaloriesPerHour() = onView(withId(R.id.caloriesPerHourValue))
    private fun onNumberPicker() = onView(withId(R.id.numberPickerNumberPicker))
    private fun onNumberPickerEditText() = onView(withParent(withId(R.id.numberPickerNumberPicker)))
    private fun onWorkoutIntensitySpinner() = onView(withId(R.id.intensitySpinner))
    private fun onWorkoutIntensitySpinnerOption(option: String) = onData(allOf(
        `is`(instanceOf(String::class.java)),
        `is`(option),
    ))

    private fun intensityLevelString(index : Int) = context.resources.getStringArray(R.array.spinnerItems)[index]

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    private val context: Context = InstrumentationRegistry.getInstrumentation().getTargetContext()

    @Before
    fun setUp() {
        onView(withId(R.id.bmr_main_button)).perform(click())
    }

    @Test
    fun changeWorkoutLength() {
        onWorkoutLength().perform(click())
        exploreNumberPicker(0, 5,120, true)

        onWorkoutLength().perform(click())
        setNumberPicker(0, 5,true)
        onWorkoutLength().check(matches(withText("0")))

        onWorkoutLength().perform(click())
        setNumberPicker(30, 5,false)
        onWorkoutLength().check(matches(not(withText("30"))))
    }

    @Test
    fun changeWorkoutsPerWeek() {
        onWorkoutsPerWeek().perform(click())
        exploreNumberPicker(0, 120, 1,true)

        onWorkoutsPerWeek().perform(click())
        setNumberPicker(0, 1,true)
        onWorkoutsPerWeek().check(matches(withText("0")))

        onWorkoutsPerWeek().perform(click())
        setNumberPicker(30, 1,false)
        onWorkoutsPerWeek().check(matches(not(withText("30"))))
    }

    @Test
    fun changeCaloriesPerHour() {
        onCaloriesPerHour().perform(click())
        exploreNumberPicker(0, 120, 10,true)

        onCaloriesPerHour().perform(click())
        setNumberPicker(0, 10, true)
        onCaloriesPerHour().check(matches(withText("0")))

        onCaloriesPerHour().perform(click())
        setNumberPicker(30, 10, false)
        onCaloriesPerHour().check(matches(not(withText("30"))))
    }

    @Test
    fun changeWorkoutIntensity() {
        onWorkoutIntensitySpinner().perform(scrollTo(), click())
        onWorkoutIntensitySpinnerOption(intensityLevelString(0)).perform(click())
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(0)))))
        onCaloriesPerHour().check(matches(withText("150")))

        onWorkoutIntensitySpinner().perform(scrollTo(), click())
        onWorkoutIntensitySpinnerOption(intensityLevelString(1)).perform(click())
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(1)))))
        onCaloriesPerHour().check(matches(withText("450")))

        onWorkoutIntensitySpinner().perform(scrollTo(), click())
        onWorkoutIntensitySpinnerOption(intensityLevelString(2)).perform(click())
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(2)))))
        onCaloriesPerHour().check(matches(withText("750")))
    }

    private fun exploreNumberPicker(min: Int, max: Int, step: Int, shouldConfirm: Boolean) {
        val realMin = min / step
        val realMax = max / step

        onNumberPicker().check(matches(isDisplayed()))

        onNumberPickerEditText().perform(click())
        Thread.sleep(500) // Wait for the keyboard to open.
        onNumberPickerEditText().perform(clearText(), typeText(realMin.toString()))
        onNumberPickerEditText().perform(pressImeActionButton())
        onNumberPickerEditText().check(matches(withText(realMin.toString())))

        onNumberPickerEditText().perform(click())
        Thread.sleep(500) // Wait for the keyboard to open.
        onNumberPickerEditText().perform(clearText(), typeText(realMax.toString()))
        onNumberPickerEditText().perform(pressImeActionButton())
        onNumberPickerEditText().check(matches(withText(realMax.toString())))

        Thread.sleep(1000) // Wait for the keyboard to close.

        onNumberPicker().perform(clickBottomCentre)
        onNumberPickerEditText().check(matches(withText(realMin.toString())))

        if(shouldConfirm)
            onView(withText(R.string.submit)).perform(click())
        else
            onView(withText(R.string.cancel)).perform(click())
    }

    private fun setNumberPicker(value: Int, step: Int, shouldConfirm: Boolean = true) {
        onNumberPickerEditText().perform(click())
        Thread.sleep(500) // Wait for the keyboard to open.
        onNumberPickerEditText().perform(clearText(), typeText((value / step).toString()))
        onNumberPickerEditText().perform(pressImeActionButton())

        Thread.sleep(500) // Wait for the keyboard to close.

        if(shouldConfirm)
            onView(withText(R.string.submit)).perform(click())
        else
            onView(withText(R.string.cancel)).perform(click())
    }

}