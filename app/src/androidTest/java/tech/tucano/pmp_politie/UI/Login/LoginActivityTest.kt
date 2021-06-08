package tech.tucano.pmp_politie.UI.Login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.*
import org.junit.runner.RunWith
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.Util.EspressoIdlingResource


@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun click_police() {
        onView(withId(R.id.btnPolitie)).perform(click())
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun test_login_admin() {
        // Information needed for the test
        val username = "admin@politie.nl";
        val password = "admin123";

        // Enter user information
        onView(withId(R.id.etEmail))
            .perform(click()).perform(ViewActions.typeText(username))
        onView(withId(R.id.etPassword))
            .perform(click())
            .perform(ViewActions.typeText(password), ViewActions.pressImeActionButton())

        // Check whether the home screen is shown
        onView(withId(R.id.homeFragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_email_domain() {
        // Information needed for the test
        val username = "admin@gmail.com"
        val errorMessage = "Voer een geldig emailadres in."

        // Fill in email with invalid domain
        onView(withId(R.id.etEmail))
            .perform(click())
            .perform(ViewActions.typeText(username), ViewActions.closeSoftKeyboard())

        // Press login button
        onView(withId(R.id.btnLogin))
            .perform(click())

        // Check if error message is visible
        onView(withId(R.id.etEmail))
            .check(matches(hasErrorText(errorMessage)))

        // Screen should not change when login fails
        onView(withId(R.id.login))
            .check(matches(isDisplayed()));
    }

    @Test
    fun test_wrong_credentials() {
        // Information needed for the test
        val username = "marcio.fuckner@politie.nl";
        val password = "ImRobocop";

        // Enter user information
        onView(withId(R.id.etEmail))
            .perform(click()).perform(ViewActions.typeText(username))
        onView(withId(R.id.etPassword))
            .perform(click())
            .perform(ViewActions.typeText(password), ViewActions.pressImeActionButton())

        // Screen should not change when login fails
        onView(withId(R.id.login)).check(matches(isDisplayed()));
    }
}