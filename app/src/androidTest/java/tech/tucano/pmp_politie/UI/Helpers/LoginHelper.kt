package tech.tucano.pmp_politie.UI.Helpers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import tech.tucano.pmp_politie.R

fun loginHelper(username: String, password: String) {
    val username = username;
    val password = password;

    onView(ViewMatchers.withId(R.id.btnPolitie)).perform(ViewActions.click())
    onView(ViewMatchers.withId(R.id.etEmail))
        .perform(ViewActions.click()).perform(ViewActions.typeText(username))
    onView(ViewMatchers.withId(R.id.etPassword))
        .perform(ViewActions.click()).perform(ViewActions.typeText(password), ViewActions.pressImeActionButton())
}