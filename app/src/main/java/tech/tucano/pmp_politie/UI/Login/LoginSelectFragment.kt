package tech.tucano.pmp_politie.UI.Login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.UI.Main.MainActivity
import tech.tucano.pmp_politie.databinding.FragmentLoginSelectBinding


class LoginSelectFragment : Fragment() {

    private lateinit var binding: FragmentLoginSelectBinding
    private lateinit var mPrefs: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentLoginSelectBinding.inflate(layoutInflater)
        mPrefs= requireContext().getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.btnPolitie.setOnClickListener{
            findNavController().navigate(R.id.action_loginSelectFragment_to_loginPolitieFragment)
        }


        binding.btnGuest.setOnClickListener {

            val mEditor: Editor = mPrefs.edit()
            mEditor.putString(getString(R.string.guest), true.toString()).apply()
            startActivity(Intent(context, MainActivity::class.java))
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            auth.signOut()}
    }

    override fun onStart() {
        super.onStart()

        if(mPrefs.getString(getString(R.string.guest), false.toString()) == true.toString()){
            startActivity(Intent(context, MainActivity::class.java))
        }
    }
}