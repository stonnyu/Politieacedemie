package tech.tucano.pmp_politie.UI.Main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tech.tucano.pmp_politie.Adapters.ArticleAdapter
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Helpers.createArticleHelper
import tech.tucano.pmp_politie.UI.Helpers.getValueHelper
import tech.tucano.pmp_politie.UI.Helpers.loginHelper
import tech.tucano.pmp_politie.UI.Helpers.stringGenerator
import tech.tucano.pmp_politie.UI.Login.LoginActivity
import tech.tucano.pmp_politie.Util.EspressoIdlingResource

const val USERNAME = "admin@politie.nl"
const val PASSWORD = "admin123"

@RunWith(AndroidJUnit4ClassRunner::class)
class ArticleFragmentTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun prepareTests() {
        // Random string for a new article
        val titleText = stringGenerator(10)

        // Detect when resources are still running
        IdlingRegistry
            .getInstance()
            .register(EspressoIdlingResource.countingIdlingResource)

        // Create an article that's marked as recent and non-confidential
        createArticleHelper(titleText, true, false)
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun test_edit_article_as_admin() {
        // Information needed
        val titleText = stringGenerator(10)
        val newTitleText = "Title is edited"

        // Login as admin
        loginHelper(USERNAME, PASSWORD)

        // Select first article in activity
        onView(withId(R.id.rvRecent))
            .perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0, click()))

        // Check if the proper screen is shown
        onView(withId(R.id.articleFragment))
            .check(matches(isDisplayed()))

        // Click edit article button
        onView(withId(R.id.btnEdit))
            .perform(scrollTo(), click())

        // Check if the proper screen is shown
        onView(withId(R.id.addArticleFragment))
            .check(matches(isDisplayed()))

        // Edit title
        onView(withId(R.id.etTitle))
            .perform(ViewActions.clearText(), ViewActions.typeText(newTitleText))

        // Close the keyboard to make the save button visible
        onView(isRoot())
            .perform(ViewActions.closeSoftKeyboard())

        // Click the save button
        onView(withId(R.id.btnSave))
            .perform(click())

        // Confirm dialog box
        clickDialogPositiveButton()

        // Select first article in activity (the edited article)
        onView(withId(R.id.rvRecent))
            .perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0, click()))

        // Check if the proper screen is shown
        onView(withId(R.id.articleFragment))
            .check(matches(isDisplayed()))

        // Check if the new title is present
        onView(Matchers
            .allOf(withId(R.id.tvTitle), withText(newTitleText), isDisplayed()))
            .check(matches(withText(newTitleText)))
    }

    @Test
    fun test_edit_topic_as_admin() {
        // Information needed
        val option = "Rechtsbijstand"

        // Login as admin
        loginHelper(USERNAME, PASSWORD)

        // Select first article in activity
        onView(withId(R.id.rvRecent))
            .perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0, click()))

        // Check if the proper screen is shown
        onView(withId(R.id.articleFragment))
            .check(matches(isDisplayed()))

        // Click edit article button
        onView(withId(R.id.btnEdit))
            .perform(click())

        // Check if the proper screen is shown
        onView(withId(R.id.addArticleFragment))
            .check(matches(isDisplayed()))

        // Save the title of the article for the assert
        val title: String = getValueHelper(onView(withId(R.id.etTitle)))

        // Open the dropdown
        onView(withId(R.id.dynamic_spinner))
            .perform(click())

        // Assign the new category
        clickOn(option)

        // Close the keyboard to make the save button visible
        onView(isRoot())
            .perform(ViewActions.closeSoftKeyboard())

        // Click the save button
        onView(withId(R.id.btnSave))
            .perform(click())

        // Confirm dialog box
        clickDialogPositiveButton()

        // Open the category
        clickOn(option)

        // Check if the proper screen is shown
        onView(withId(R.id.topicToArticlesFragment))
            .check(matches(isDisplayed()))

        // Check if the title of the article is present in the specified category
        assertDisplayed(title)
    }

    @Test
    fun test_delete_article_as_admin() {
        // Login as admin
        loginHelper(USERNAME, PASSWORD)

        // Select the newly created article in activity
        onView(withId(R.id.rvRecent))
            .perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0, click()))

        // Check if the article is shown
        onView(withId(R.id.articleFragment))
            .check(matches(isDisplayed()))

        // Save the title of the article for the assert
        val title: String = getValueHelper(onView(withId(R.id.tvTitle)))

        // Delete article
        onView(withId(R.id.btnDelete))
            .perform(scrollTo(), click())

        // Confirm the dialog
        clickDialogPositiveButton()

        // Select the most recent article activity
        onView(withId(R.id.rvRecent))
            .perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0, click()))

        onView(Matchers
            .allOf(withId(R.id.tvTitle), withText(title), isDisplayed()))
            .check(doesNotExist())
    }

    @Test
    fun test_edit_article_as_guest() {
        // Click edit article button
        onView(withId(R.id.btnGuest)).perform(click())

        // Select first article in activity
        onView(withId(R.id.rvRecent)).perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0,
            click()))

        // Check if the proper screen is shown
        onView(withId(R.id.articleFragment)).check(matches(isDisplayed()))

        // Edit button should not be visible to guests
        onView(withId(R.id.btnEdit)).check(matches(not(isDisplayed())))
    }

    @Test
    fun test_delete_article_as_guest() {
        // Sign in as guest user
        onView(withId(R.id.btnGuest))
            .perform(click())

        // Select first article in activity
        onView(withId(R.id.rvRecent))
            .perform(actionOnItemAtPosition<ArticleAdapter.ViewHolder>(0, click()))

        // Check if the proper screen is shown
        onView(withId(R.id.articleFragment))
            .check(matches(isDisplayed()))

        // Edit button should not be visible to guests
        onView(withId(R.id.btnDelete))
            .check(matches(not(isDisplayed())))
    }
}