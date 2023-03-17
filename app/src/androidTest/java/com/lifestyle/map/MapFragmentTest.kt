package com.lifestyle.map


import android.location.Location
import android.view.InputDevice
import android.view.MotionEvent
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
class MapFragmentTest {
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
    private fun onLocationButton() = onView(withId(R.id.useGPSLocationButton))
    private fun onSubmitButton() = onView(withId(R.id.submitNewLocationButton))
    private fun onMapButton() = onView(withId(R.id.gotoMapButton))
    private fun onCountry() = onView(withId(R.id.CountryEditText))
    private fun onState() = onView(withId(R.id.StateEditText))
    private fun onCity() = onView(withId(R.id.CityEditText))
    private fun onAddress() = onView(withId(R.id.StreetEditText))
    private fun onZipCode() = onView(withId(R.id.ZipCodeEditText))

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()
    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    @Before
    fun setUp() {
        activityScenarioRule.scenario.onActivity { activity ->
            var user = (activity as UserProvider).getUser()
            user.location = null
            user.textLocation = TextLocation()
        }

        onView(withId(R.id.hikes_main_button)).perform(click())
    }

    @Test
    fun enterLocation() {
        val country = "United States"
        val state = "Utah"
        val city = "Salt Lake City"
        val address = "201 Presidents' Cir"
        val postal = "84112"
        val expectedLocation = Location("")
        expectedLocation.latitude = 40.76493552282655
        expectedLocation.longitude = -111.84209976524588
        val maxLocationDelta = 1000f // meters

        onCountry().perform(scrollTo(), clearText(), typeText(country), pressImeActionButton(), closeSoftKeyboard())
        onCountry().check(matches(withText(country)))
        onState().perform(scrollTo(), clearText(), typeText(state), pressImeActionButton(), closeSoftKeyboard())
        onState().check(matches(withText(state)))
        onCity().perform(scrollTo(), clearText(), typeText(city), pressImeActionButton(), closeSoftKeyboard())
        onCity().check(matches(withText(city)))
        onAddress().perform(scrollTo(), clearText(), typeText(address), pressImeActionButton(), closeSoftKeyboard())
        onAddress().check(matches(withText(address)))
        onZipCode().perform(scrollTo(), clearText(), typeText(postal), pressImeActionButton(), closeSoftKeyboard())
        onZipCode().check(matches(withText(postal)))

        onSubmitButton().perform(scrollTo(), click())
        activityScenarioRule.scenario.onActivity { activity ->
            val user = (activity as UserProvider).getUser()
            assertNotNull(user.location)
            val delta = user.location!!.distanceTo(expectedLocation)
            assertTrue("Actual location is within ${maxLocationDelta}m of expected location", delta<=maxLocationDelta)
            assertEquals(country, user.textLocation.country)
            assertEquals(state,   user.textLocation.state)
            assertEquals(city,    user.textLocation.city)
            assertEquals(address, user.textLocation.streetAddress)
            assertEquals(postal,  user.textLocation.zipCode)
        }
    }

    @Test
    fun updateLocation() {
        val country = "United States"
        val state = "California"
        val city = "Mountain View"
        val address = "1600 Amphitheatre Pkwy"
        val postal = "94043"
        val expectedLocation = Location("")
        expectedLocation.latitude = 37.42203147727621
        expectedLocation.longitude = -122.08410043028806

        onLocationButton().perform(scrollTo(), click())

        //Thread.sleep(1000)

        activityScenarioRule.scenario.onActivity { activity ->
            val user = (activity as UserProvider).getUser()
            assertNotNull(user.location)
            val delta = user.location!!.distanceTo(expectedLocation)
            val maxDelta = 1.5f * user.location!!.accuracy
            assertTrue("Actual location is within ${maxDelta}m of expected location", delta<=maxDelta)
            assertEquals(country, user.textLocation.country)
            assertEquals(state,   user.textLocation.state)
            assertEquals(city,    user.textLocation.city)
            assertEquals(address, user.textLocation.streetAddress)
            assertEquals(postal,  user.textLocation.zipCode)
        }
    }

}