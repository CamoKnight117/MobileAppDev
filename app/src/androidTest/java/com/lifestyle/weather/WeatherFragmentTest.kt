package com.lifestyle.weather

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.lifestyle.R
import com.lifestyle.main.MainActivity
import com.lifestyle.main.UserProvider
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherFragmentTest {
    private fun onLocation() = onView(withId(R.id.weatherLocationTextView))
    private fun onWeather() = onView(withId(R.id.weatherWeatherTextView))
    private fun onTemperature() = onView(withId(R.id.weatherTemperatureTextView))

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()
    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private val context: Context = InstrumentationRegistry.getInstrumentation().getTargetContext()

    @Before
    fun setUp() {
        activityScenarioRule.scenario.onActivity { activity ->
            (activity as UserProvider).getUser().location = null
        }

        //Thread.sleep(3000)

        onView(withId(R.id.weather_main_button)).perform(click())
    }

    @Test
    fun updateLocation() {

        //Thread.sleep(3000)

        onLocation().perform(click())
        onLocation().check(matches(not(withText("None"))))
        onLocation().check(matches(not(withText(""))))
        onWeather().check(matches(not(withText(""))))
        onTemperature().check(matches(not(withText(""))))
    }
}