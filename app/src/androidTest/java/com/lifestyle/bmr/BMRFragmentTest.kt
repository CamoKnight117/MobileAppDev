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

    private fun onHeaderName() = onView(withId(R.id.nameTextValue))
    private fun onHeaderAgeAndSex() = onView(withId(R.id.ageAndSexValue))
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
        val onEditText = onWorkoutLength()
        editAndCheckText(onEditText, "0", true)
        editAndCheckText(onEditText, "10", true)
        editAndCheckText(onEditText, "100", true)
        editAndCheckText(onEditText, "1000", true)
        editAndCheckText(onEditText, "10000", true)
        editAndCheckText(onEditText, "100000", false)
        editAndCheckText(onEditText, ".5", true)
        editAndCheckText(onEditText, "5.5", true)
        editAndCheckText(onEditText, "50.5", true)
        editAndCheckText(onEditText, "500.5", true)
        editAndCheckText(onEditText, "5000.5", false)
    }

    @Test
    fun changeWorkoutsPerWeek() {
        val onEditText = onWorkoutsPerWeek()
        editAndCheckText(onEditText, "0", true)
        editAndCheckText(onEditText, "10", true)
        editAndCheckText(onEditText, "100", false)
        editAndCheckText(onEditText, "1000", false)
        editAndCheckText(onEditText, "10000", false)
        editAndCheckText(onEditText, "100000", false)
        //editAndCheckText(onEditText, ".5", false)
        //editAndCheckText(onEditText, "5.5", false)
        //editAndCheckText(onEditText, "50.5", false)
        //editAndCheckText(onEditText, "500.5", false)
        //editAndCheckText(onEditText, "5000.5", false)
    }

    @Test
    fun changeCaloriesPerHour() {
        val onEditText = onCaloriesPerHour()
        editAndCheckText(onEditText, "0", true)
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(0))))) // < 300
        editAndCheckText(onEditText, "10", true)
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(0))))) // < 300
        editAndCheckText(onEditText, "100", true)
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(0))))) // < 300
        editAndCheckText(onEditText, "500", true)
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(1))))) // < 600
        editAndCheckText(onEditText, "1000", true)
        onWorkoutIntensitySpinner().check(matches(withSpinnerText(`is`(intensityLevelString(2))))) // >= 600
        editAndCheckText(onEditText, "10000", false)
        editAndCheckText(onEditText, "100000", false)
        //editAndCheckText(onEditText, ".5", false)
        //editAndCheckText(onEditText, "5.5", false)
        //editAndCheckText(onEditText, "50.5", false)
        //editAndCheckText(onEditText, "500.5", false)
        //editAndCheckText(onEditText, "5000.5", false)
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

    companion object {
        private fun editAndCheckText(onEditText: ViewInteraction, text: String, shouldSucceed: Boolean) {
            try {
                onEditText.perform(scrollTo(), click(), replaceText(text), pressImeActionButton(), closeSoftKeyboard())

                if(shouldSucceed)
                    onEditText.check(matches(withText(text)))
                else
                    onEditText.check(matches(not(withText(text))))
            } catch(e : java.lang.NumberFormatException) {
                // The EditText interacted with does not accept the given String input.
                assertFalse(shouldSucceed);
            }
        }
    }

}