package tech.tucano.pmp_politie.UI.Main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import tech.tucano.pmp_politie.R


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        /**
         * Getting the shared preferences from login fragments.
         */
        mPrefs= getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE)


        /**
         * If the guest has logged in, make the nav_view invisible.
         */
        if((mPrefs.getString(getString(R.string.guest), "false") == true.toString())) {
            nav_view.visibility = View.GONE
            nav_view_guest.visibility = View.VISIBLE
            setNavigations(R.id.nav_view_guest)
        } else {
            nav_view.visibility = View.VISIBLE
            nav_view_guest.visibility = View.GONE
            setNavigations(R.id.nav_view)
        }
    }

    /**
     * Bottom Navigation, the navigation of each button.
     */
    private fun setNavigations(navViewSet: Int) {
        val navView: BottomNavigationView = findViewById(navViewSet)
        navView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.mi_home -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.mi_profile -> {
                    navController.navigate(R.id.profileFragment)
                }
                R.id.mi_login -> {
                    //Log out the guest user.
                    val mEditor: SharedPreferences.Editor = mPrefs.edit()
                    mEditor.putString(getString(R.string.guest), false.toString()).apply()

                    navController.navigate(R.id.loginActivity)
                }
            }
            true
        }
    }
}