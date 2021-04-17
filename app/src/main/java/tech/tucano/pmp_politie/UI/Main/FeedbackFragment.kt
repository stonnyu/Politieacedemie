package tech.tucano.pmp_politie.UI.Main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_feedback.*
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.FragmentFeedbackBinding
import java.io.BufferedReader

private const val TAG: String = "FeedbackFragment"
private const val FILE_NAME = "email.txt"

class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedbackBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // Return to previous screen
        fabBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun init() {

        binding.btnSendFeedback.setOnClickListener {
            sendFeedback()

            // Provide feedback when the button is pressed
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }

            builder?.setMessage(getString(R.string.feedback_alertdialog_message))
                ?.setTitle(getString(R.string.feedback_alertdialog_title))

            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }
    }

    /**
     * Reads email address from email.txt
     * @return String containing email address
     */
    private fun readTextFile() : String {
        return requireContext()
            .assets
            .open(FILE_NAME)
            .bufferedReader()
            .use(BufferedReader::readText)
    }

    /**
     * Converting user input into data that is compatible with the Firebase Trigger Mail extension
     * @return data containing mail message information
     */
    private fun feedbackParser() : HashMap<String, String> {

        // Get user input
        val feedbackEmail = binding.etFeedbackEmail.text.toString()
        val feedbackSubject = binding.etFeedbackSubject.text.toString()
        val feedbackText = binding.etFeedbackText.text.toString()

        val emailSubject =
            java.lang.String.format("Feedback: %s, %s", feedbackEmail, feedbackSubject)

        val data : HashMap<String, String> = HashMap()
        data["subject"] = emailSubject
        data["html"] = feedbackText

        return data
    }

    /**
     * Store the mail in the Cloud Firestore.
     */
    private fun sendFeedback() {

        // Firebase Trigger Email extension is only compatible with Cloud Firestore
        val db = FirebaseFirestore.getInstance()

        // Setup mail to be sent to database
        val message = feedbackParser()
        val mail = hashMapOf(
            "to" to readTextFile(),
            "message" to message
        )

        // Add a new mail document with a generated ID
        db.collection("mail")
            .add(mail)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Document added in Cloud with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document in Cloud Firestore", e)
            }
    }
}