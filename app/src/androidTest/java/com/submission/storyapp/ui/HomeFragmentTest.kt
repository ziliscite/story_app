package com.submission.storyapp.ui

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.submission.storyapp.HiltTestActivity
import com.submission.storyapp.R
import com.submission.storyapp.presentation.core.home.HomeFragment
import com.submission.storyapp.utils.EspressoIdlingResource
import com.submission.storyapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(HiltTestActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun userLogout_Confirmed() {
        launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withText(R.string.confirmation)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_positive_button)).perform(click())

        assertTrue(activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun userLogout_Canceled() {
        launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withText(R.string.confirmation)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_negative_button)).perform(click())
    }
}
