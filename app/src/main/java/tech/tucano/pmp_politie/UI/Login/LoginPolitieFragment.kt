package tech.tucano.pmp_politie.UI.Login


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login_politie.*
import tech.tucano.pmp_politie.DataModel.ConfidentialTopicModel
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Main.MainActivity
import tech.tucano.pmp_politie.databinding.FragmentLoginPolitieBinding
import java.util.*
import java.util.regex.Pattern


class LoginPolitieFragment : Fragment() {

    private lateinit var binding: FragmentLoginPolitieBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentLoginPolitieBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        mPrefs= requireContext().getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE)

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.btnLogin.setOnClickListener {
            doLogin()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginPolitieFragment_to_loginSelectFragment)
        }


        //Check if the done button on the keyboard is pressed in order to log in.
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                btnLogin.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun doLogin() {
        //Check if the etEmail field is empty
        if (etEmail.text.toString().isEmpty()) {
            etEmail.error = getString(R.string.missing_email)
            etEmail.requestFocus()
            return
        }

        //Check if the input of the etEmail field is a valid email address
        if (!isValidEmailString(etEmail.text.toString().toLowerCase(Locale.ROOT))) {
            etEmail.error = getString(R.string.invalid_email)
            etEmail.requestFocus()
            return
        }

        //Check if the etPassword field is empty
        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = getString(R.string.missing_password)
            etPassword.requestFocus()
            return
        }

        //Attempt to sign in using user credentials.
        auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    //Move the app along to the MainActivity
                    startActivity(Intent(context, MainActivity::class.java))
                    Log.d(ContentValues.TAG, "signInWithEmail:success")

                    //Show that the user successfully logged in to their account.
                    val userMail = auth.currentUser!!.email
                    Toast.makeText(
                        requireContext(), getString(R.string.login_successful, userMail),
                        Toast.LENGTH_SHORT
                    ).show()

                    val currentUser = auth.currentUser

                    if (currentUser != null) {
                        ConfidentialTopicModel.reset()
                    }

                    //Set the guest preference to false.
                    val mEditor: SharedPreferences.Editor = mPrefs.edit()
                    mEditor.putString(getString(R.string.guest), false.toString()).apply()

                } else {
                    //If sign in fails, display a message to the user.
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                }
            }

    }


    //Check if the email address matches the given pattern.
    private fun isValidEmailString(emailString: String): Boolean {
        val emailRegex = ("^[A-Za-z0-9._%+\\-]+@politie+\\.nl$")

        return emailString.isNotEmpty() && Pattern.compile(emailRegex).matcher(emailString).matches()
    }

}