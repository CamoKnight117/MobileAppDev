package com.lifestyle.profile

import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lifestyle.R
import com.lifestyle.main.MainActivity
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
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

    fun onHeaderName() = onView(withId(R.id.nameTextValue))
    fun onHeaderAgeAndSex() = onView(withId(R.id.ageAndSexValue))
    fun onName() = onView(withId(R.id.profileName))
    fun onAge() = onView(withId(R.id.profileAge))
    fun onHeight() = onView(withId(R.id.profileHeight))
    fun onWeight() = onView(withId(R.id.profileWeight))
    fun onNumberPicker() = onView(withId(R.id.numberPickerNumberPicker))
    fun onNumberPickerEditText() = onView(withParent(withId(R.id.numberPickerNumberPicker)))
    fun onSexSpinner() = onView(withId(R.id.profileSex))
    fun onSexSpinnerOption(option: String) = onData(allOf(
        `is`(instanceOf(String::class.java)),
        `is`(option),
        //withParent(withId(R.id.profileSex))
    ))

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Before
    fun setUp() {
        onView(withId(R.id.prof_button)).perform(click())
    }

    @Test
    fun changeName() {
        val name1 = "John Doe III"
        onName().perform(scrollTo(), clearText(), typeText(name1), pressImeActionButton(), closeSoftKeyboard())
        onName().check(matches(withText(name1)))
        onHeaderName().check(matches(withText(name1)))
        val name2 = "Matthias V."
        onName().perform(scrollTo(), clearText(), typeText(name2), pressImeActionButton(), closeSoftKeyboard())
        onName().check(matches(withText(name2)))
        onHeaderName().check(matches(withText(name2)))
    }

    @Test
    fun changeAge() {
        onAge().perform(click())
        exploreNumberPicker(0, 120, true)

        onAge().perform(click())
        setNumberPicker(0, true)
        onAge().check(matches(withText("0 yr.")))
        onHeaderAgeAndSex().check(matches(withText("0 M")))

        onAge().perform(click())
        setNumberPicker(30, false)
        onAge().check(matches(not(withText("30 yr."))))
        onHeaderAgeAndSex().check(matches(withText("0 M")))
    }

    @Test
    fun changeHeight() {
        onHeight().perform(click())
        exploreNumberPicker(12, 120, true)

        onHeight().perform(click())
        setNumberPicker(12, true)
        onHeight().check(matches(withText("12 in.")))

        onHeight().perform(click())
        setNumberPicker(30, false)
        onHeight().check(matches(not(withText("30 in."))))
    }

    @Test
    fun changeWeight() {
        onWeight().perform(click())
        exploreNumberPicker(10, 1000, true)

        onWeight().perform(click())
        setNumberPicker(10, true)
        onWeight().check(matches(withText("10 lb.")))

        onWeight().perform(click())
        setNumberPicker(30, false)
        onWeight().check(matches(not(withText("30 lb."))))
    }

    @Test
    fun changeSex() {
        onSexSpinner().perform(scrollTo(), click())
        onSexSpinnerOption("F").perform(click())
        onSexSpinner().check(matches(withSpinnerText(containsString("F"))))
        onHeaderAgeAndSex().check(matches(withText(object : BaseMatcher<String>() {
            override fun describeTo(description: Description?) { }
            override fun matches(text: Any) = text is String && text.endsWith("F")
        })))

        onSexSpinner().perform(scrollTo(), click())
        onSexSpinnerOption("M").perform(click())
        onSexSpinner().check(matches(withSpinnerText(containsString("M"))))
        onHeaderAgeAndSex().check(matches(withText(object : BaseMatcher<String>() {
            override fun describeTo(description: Description?) { }
            override fun matches(text: Any) = text is String && text.endsWith("M")
        })))
    }

    private fun exploreNumberPicker(min: Int, max: Int, shouldConfirm: Boolean) {
        onNumberPicker().check(matches(isDisplayed()))

        onNumberPickerEditText().perform(click())
        Thread.sleep(500) // Wait for the keyboard to open.
        onNumberPickerEditText().perform(clearText(), typeText(min.toString()))
        onNumberPickerEditText().perform(pressImeActionButton())
        onNumberPickerEditText().check(matches(withText(min.toString())))

        onNumberPickerEditText().perform(click())
        Thread.sleep(500) // Wait for the keyboard to open.
        onNumberPickerEditText().perform(clearText(), typeText(max.toString()))
        onNumberPickerEditText().perform(pressImeActionButton())
        onNumberPickerEditText().check(matches(withText(max.toString())))

        Thread.sleep(1000) // Wait for the keyboard to close.

        onNumberPicker().perform(clickBottomCentre)
        onNumberPickerEditText().check(matches(withText(min.toString())))

        if(shouldConfirm)
            onView(withText(R.string.submit)).perform(click())
        else
            onView(withText(R.string.cancel)).perform(click())
    }

    private fun setNumberPicker(value: Int, shouldConfirm: Boolean = true) {
        onNumberPickerEditText().perform(click())
        Thread.sleep(500) // Wait for the keyboard to open.
        onNumberPickerEditText().perform(clearText(), typeText(value.toString()))
        onNumberPickerEditText().perform(pressImeActionButton())

        Thread.sleep(500) // Wait for the keyboard to close.

        if(shouldConfirm)
            onView(withText(R.string.submit)).perform(click())
        else
            onView(withText(R.string.cancel)).perform(click())
    }

}