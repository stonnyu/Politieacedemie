package tech.tucano.pmp_politie.UI.Main


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.interaction.BaristaKeyboardInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Helpers.loginHelper
import tech.tucano.pmp_politie.UI.Login.LoginActivity
import tech.tucano.pmp_politie.Util.EspressoIdlingResource
import java.util.concurrent.TimeUnit

private const val SEARCH_KEYWORD = "vertrouwlijk"
private const val CONFIDENTIAL_ARTICLE_TITLE = "Een vertrouwlijk"

@RunWith(AndroidJUnit4ClassRunner::class)
class SearchFragmentTest {

    @get: Rule
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun test_confidential_admin_search() {

        // Log in as admin
        loginHelper("admin@politie.nl", "admin123")

        // Click search button
        onView(
            allOf(withId(R.id.fabSearch),
                childAtPosition(
                    allOf(withId(R.id.homeFragment),
                        childAtPosition(
                            withId(R.id.nav_host_fragment),
                            0)),
                    4),
                isDisplayed())).perform(click())

        // Type keyword
        onView(
            allOf(withId(R.id.search_field),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0),
                    0)
            )).perform(replaceText(SEARCH_KEYWORD), closeSoftKeyboard())

        // Check if exists
        onView(
            allOf(withId(R.id.tvTopic), withText(CONFIDENTIAL_ARTICLE_TITLE),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed())).check(matches(withText(CONFIDENTIAL_ARTICLE_TITLE)))


    }

    @Test
    fun test_confidential_guest_search() {

        // Log in as guest
        onView(withId(R.id.btnGuest)).perform(click())

        // Click search button
        onView(
            allOf(withId(R.id.fabSearch),
                childAtPosition(
                    allOf(withId(R.id.homeFragment),
                        childAtPosition(
                            withId(R.id.nav_host_fragment),
                            0)),
                    4),
                isDisplayed())).perform(click())

        // Type keyword
        onView(
            allOf(withId(R.id.search_field),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0),
                    0),
                isDisplayed())).perform(replaceText(SEARCH_KEYWORD), closeSoftKeyboard())

        // Check if doesn't exists
        onView(
            allOf(withId(R.id.tvTopic), withText(CONFIDENTIAL_ARTICLE_TITLE),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed())).check(doesNotExist())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int,
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}
