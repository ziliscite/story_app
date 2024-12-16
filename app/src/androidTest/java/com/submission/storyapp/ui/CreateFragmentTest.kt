package com.submission.storyapp.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.Navigation
import android.net.Uri
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.submission.storyapp.R
import com.submission.storyapp.utils.EspressoIdlingResource
import com.submission.storyapp.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.submission.storyapp.presentation.core.create.CreateFragment
import com.submission.storyapp.presentation.core.create.CreateFragmentDirections
import com.submission.storyapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.allOf

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest // Quite difficult to set up Hilt in UI tests, might as well try to test thoroughly
@MediumTest
class CreateFragmentTest {
    private val mockWebServer = MockWebServer()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
        mockWebServer.start(8080)
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun uploadSuccess() {
        val navController = mockk<NavController>(relaxed = true)

        lateinit var fragment: CreateFragment
        launchFragmentInHiltContainer<CreateFragment>{
            fragment = this

            Navigation.setViewNavController(requireView(), navController)
        }

        val resultData = Intent()
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        val galleryIntentMatcher = allOf(
            IntentMatchers.hasAction("android.provider.action.PICK_IMAGES"),
            IntentMatchers.hasType("image/*")
        )
        Intents.intending(galleryIntentMatcher).respondWith(result)

        onView(withId(R.id.btnGallery)).perform(click())
        Intents.intended(galleryIntentMatcher)

        // Simulate returning from gallery
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            fragment.onActivityResult(1001, Activity.RESULT_OK, resultData)
        }

        onView(withId(R.id.btnGallery)).check(matches(isDisplayed()))

        val cameraIntentMatcher = IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)
        Intents.intending(cameraIntentMatcher).respondWith(result)

        onView(withId(R.id.btnCamera)).perform(click())
        Intents.intended(cameraIntentMatcher)

        // Simulate returning from camera
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            fragment.onActivityResult(1002, Activity.RESULT_OK, resultData)
        }

        onView(withId(R.id.btnCamera)).check(matches(isDisplayed()))

        // Simulate taking a photo from camera
        val mockUri = Uri.parse("android.resource://com.submission.storyapp/" + R.drawable.app_logo)
        fragment.setImageForTesting(mockUri)

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.etDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.ivStory)).check(matches(isDisplayed()))
        onView(withId(R.id.checkboxLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.etDescription)).perform(typeText("Description to my description"), closeSoftKeyboard())

        val mockResponse = MockResponse().setResponseCode(200).setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.btnUpload)).perform(scrollTo(), click())

        // Verify if navigated to HomeFragment after successful upload
        verify {
            navController.navigate(CreateFragmentDirections.actionCreateFragmentToHomeFragment())
        }
    }

    @Test
    fun uploadError() {
        launchFragmentInHiltContainer<CreateFragment>()

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnGallery)).check(matches(isDisplayed()))
        onView(withId(R.id.btnCamera)).check(matches(isDisplayed()))
        onView(withId(R.id.etDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.ivStory)).check(matches(isDisplayed()))
        onView(withId(R.id.checkboxLayout)).check(matches(isDisplayed()))

        val mockResponse = MockResponse().setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.btnUpload)).perform(scrollTo(), click())
    }
}
