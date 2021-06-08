package tech.tucano.pmp_politie.UI.Helpers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import com.schibsted.spain.barista.interaction.BaristaKeyboardInteractions.closeKeyboard
import tech.tucano.pmp_politie.R

fun createArticleHelper(title: String, isRecent: Boolean, isConfidential: Boolean) {
    loginHelper("admin@politie.nl", "admin123")
    // Navigate to AddArticleFragment
    clickOn("Profiel")
    clickOn("Nieuw artikel")

    // Highlight article
    if(isRecent) {
        onView(ViewMatchers.withId(R.id.recentSwitch)).perform(ViewActions.click())
    }

    if(isConfidential) {
        onView(ViewMatchers.withId(R.id.confidentialSwitch)).perform(ViewActions.click())
    }

    // Fill in required fields
    onView(ViewMatchers.withId(R.id.etTitle))
        .perform(ViewActions.clearText(), ViewActions.typeText(title))

    // Save article
    closeKeyboard()
    onView(ViewMatchers.withId(R.id.btnSave))
        .perform(ViewActions.click())
    clickDialogPositiveButton()

    // Sign out
    clickOn("Profiel")
    clickOn("Log uit")
}