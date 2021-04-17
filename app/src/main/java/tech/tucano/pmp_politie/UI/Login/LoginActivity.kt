package tech.tucano.pmp_politie.UI.Login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import tech.tucano.pmp_politie.R

private var PERSISTENCE_ENABLED = false

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PERSISTENCE_ENABLED) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            PERSISTENCE_ENABLED = true
        }

        setContentView(R.layout.activity_login)
    }
}
