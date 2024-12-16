package com.submission.storyapp.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.submission.storyapp.R
import com.submission.storyapp.presentation.auth.login.LoginFragment
import com.submission.storyapp.utils.EspressoIdlingResource
import com.submission.storyapp.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.submission.storyapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@MediumTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        hiltRule.inject()
        mockWebServer.start(8080)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun userLogin_Success() {
        launchFragmentInHiltContainer<LoginFragment>()

        onView(withId(R.id.imageView)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.messageTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_in_text_view)).check(matches(isDisplayed()))

        onView(withId(R.id.emailEditText)).perform(typeText("abcdefgh@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText("11111111"), closeSoftKeyboard())

        val mockResponse = MockResponse().setResponseCode(200).setBody(JsonConverter.readStringFromFile("success_response_login.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.sign_in_button)).perform(click())
    }

    @Test
    fun userLogin_Failed() {
        launchFragmentInHiltContainer<LoginFragment>()

        onView(withId(R.id.imageView)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.messageTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.sign_in_text_view)).check(matches(isDisplayed()))

        val mockResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.sign_in_button)).perform(click())
    }
}
