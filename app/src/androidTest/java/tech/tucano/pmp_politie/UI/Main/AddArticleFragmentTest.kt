package tech.tucano.pmp_politie.UI.Main

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaDialogInteractions
import com.schibsted.spain.barista.interaction.BaristaKeyboardInteractions
import org.hamcrest.Description
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tech.tucano.pmp_politie.Adapters.ArticleAdapter
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Helpers.loginHelper
import tech.tucano.pmp_politie.UI.Login.LoginActivity
import tech.tucano.pmp_politie.Util.EspressoIdlingResource

private const val TITLE_TEXT = "Loremipsum"

@RunWith(AndroidJUnit4::class)
class AddArticleFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun prepareTests() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        loginHelper("admin@politie.nl", "admin123")
    }

    @After
    fun cleanup() {
        onView(withId(R.id.btnDelete)).perform(click())
        clickOn("JA")

        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun test_highlight_new_article() {
        // Navigate to AddArticleFragment
        clickOn("Profiel")
        clickOn("Nieuw artikel")

        // Highlight article
        onView(withId(R.id.recentSwitch)).perform(click())

        // Fill in required fields
        onView(withId(R.id.etTitle))
            .perform(ViewActions.clearText(), ViewActions.typeText(TITLE_TEXT))

        // Save article
        BaristaKeyboardInteractions.closeKeyboard()
        onView(withId(R.id.btnSave))
            .perform(click())
        BaristaDialogInteractions.clickDialogPositiveButton()

        // Test new article in highlights
        onView(withId(R.id.rvRecent))
            .perform(scrollTo<ArticleAdapter.ViewHolder>(withText(TITLE_TEXT)))
        onView(withId(R.id.rvRecent))
            .perform(actionOnItem<ArticleAdapter.ViewHolder>(
                hasDescendant(withText(TITLE_TEXT)), click()))
    }

    fun withText(title: String) = object : BoundedMatcher<View, View>(View::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("Searching for title with: $title")
        }

        override fun matchesSafely(item: View?): Boolean {
            val views = ArrayList<View>()
            item?.findViewsWithText(views, title, View.FIND_VIEWS_WITH_TEXT)

            return when {
                views.size == 1 -> true
                else -> false
            }
        }
    }

}