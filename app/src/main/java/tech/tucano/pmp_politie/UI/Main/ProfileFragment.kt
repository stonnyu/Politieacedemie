package tech.tucano.pmp_politie.UI.Main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Login.LoginActivity


class ProfileFragment : PreferenceFragmentCompat() {

    private lateinit var auth: FirebaseAuth

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        auth = FirebaseAuth.getInstance()

        setProfileInfo()

        arguments?.putString(ARG_RECENT_ARTICLE_ID, "")
    }

    //Function for detecting preference clicks
    override fun onPreferenceTreeClick(preference: Preference): Boolean {

        //Check which key is pressed and perform a corresponding action
        when (preference.key) {
            getString(R.string.pref_key_sign_out) -> signOut()
            getString(R.string.pref_key_feedback) ->
                findNavController().navigate(R.id.feedbackFragment)
            getString(R.string.pref_key_add_article) -> findNavController().navigate(R.id.addArticleFragment)
        }

        return true
    }

    private fun setProfileInfo() {
        //Find the correct preference
        val emailPreference: Preference? = findPreference("profile_email")

        //Set the summary to the currently logged in user mail
        emailPreference?.summary = auth.currentUser?.email
    }


    private fun signOut() {
        auth.signOut()

        //Set the logged in guest variable to false.
        requireContext().getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE).edit()
            .putString(getString(R.string.guest), false.toString()).apply()

        //If the current user is null it means the sign out was successful
        if (auth.currentUser == null) {
            Toast.makeText(context, getString(R.string.successful_sign_out), Toast.LENGTH_SHORT)
                .show()

            //Go to the LoginActivity
            startActivity(Intent(context, LoginActivity::class.java))

        } else {
            Toast.makeText(context, getString(R.string.unsuccessful_sign_out), Toast.LENGTH_SHORT)
                .show()
        }
    }
}