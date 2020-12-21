package nl.svdoetelaar.capstoneproject.ui.landing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import nl.svdoetelaar.capstoneproject.databinding.ActivityLoginBinding
import nl.svdoetelaar.capstoneproject.ui.overview.OverviewActivity
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.REQUEST_CODE_SIGN_IN
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.user

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), REQUEST_CODE_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                user = FirebaseAuth.getInstance().currentUser

                startActivity(
                    Intent(this, OverviewActivity::class.java)
                )
            } else {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), REQUEST_CODE_SIGN_IN
                )
            }
        }
    }

}