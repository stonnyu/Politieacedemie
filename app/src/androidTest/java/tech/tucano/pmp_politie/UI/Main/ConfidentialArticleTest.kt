package tech.tucano.pmp_politie.UI.Main

import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import com.schibsted.spain.barista.interaction.BaristaKeyboardInteractions.closeKeyboard
import org.hamcrest.Matchers
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tech.tucano.pmp_politie.Adapters.ArticleAdapter
import tech.tucano.pmp_politie.Adapters.TopicAdapter
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Helpers.loginHelper
import tech.tucano.pmp_politie.UI.Login.LoginActivity
import tech.tucano.pmp_politie.Util.EspressoIdlingResource

private const val CONFIDENTIAL_ARTICLE_TITLE = "Confidential"
private const val CONFIDENTIAL_TOPIC_NAME = "Locaties"

@RunWith(AndroidJUnit4::class)
class ConfidentialArticleTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun test_find_confidential_topic_as_guest() {
        // Log in as guest
        onView(withId(R.id.btnGuest)).perform(click())

        // Check if doesn't exists
        onView(
            Matchers.allOf(withId(R.id.tvTopic), withText(CONFIDENTIAL_TOPIC_NAME),
                withParent(withParent(IsInstanceOf.instanceOf(FrameLayout::class.java))),
                isDisplayed())).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun test_open_confidential_article_as_admin() {
        // Login as admin
        loginHelper("admin@politie.nl", "admin123")

        // Navigate to AddArticleFragment
        clickOn("Profiel")
        clickOn("Nieuw artikel")

        // Mark article as confidential
        onView(withId(R.id.confidentialSwitch)).perform(click())
        onView(withId(R.id.confidentialSwitch)).check(matches(isChecked()))

        // Fill in required fields
        onView(withId(R.id.etTitle)).perform(clearText(), typeText(CONFIDENTIAL_ARTICLE_TITLE))

        // Save article
        closeKeyboard()
        onView(withId(R.id.btnSave)).perform(click())
        clickDialogPositiveButton()

        // Check if confidential topic exists
        onView(withId(R.id.rvTopic)).perform(actionOnItem<TopicAdapter.ViewHolder>(hasDescendant(
            withText(CONFIDENTIAL_TOPIC_NAME)), click()))
        onView(withId(R.id.rvArticleList)).perform(actionOnItem<ArticleAdapter.ViewHolder>(
            hasDescendant(withText(
                CONFIDENTIAL_ARTICLE_TITLE)),
            click()))

        // Cleanup
        onView(withId(R.id.btnDelete)).perform(click())
        clickOn("JA")
    }

}