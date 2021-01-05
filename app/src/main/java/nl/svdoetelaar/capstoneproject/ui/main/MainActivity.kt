package nl.svdoetelaar.capstoneproject.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import nl.svdoetelaar.capstoneproject.databinding.ActivityMainBinding
import nl.svdoetelaar.capstoneproject.util.LoginUtil
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.providers
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.user
import nl.svdoetelaar.capstoneproject.util.PermissionUtil
import nl.svdoetelaar.capstoneproject.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
//    lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        geofencingClient = LocationServices.getGeofencingClient(this)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (user == null) {
            startLoginActivity()
        }

    }

    private fun startLoginActivity() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), LoginUtil.REQUEST_CODE_SIGN_IN
        )
    }


    private fun checkRequirements() {
        if (user == null) {
            binding.cardAskLogin.root.visibility = View.VISIBLE
            binding.cardAskLogin.btnRequest.setOnClickListener {
                startLoginActivity()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_FOREGROND_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (PermissionUtil.hasPermissionForegroundLocation()) {
                        binding.cardAskForegroundLocation.root.visibility = View.GONE
                    }
                }
            }
            PermissionUtil.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (PermissionUtil.hasPermissionBackgroundLocation()) {
                        binding.cardAskBackgroundLocation.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LoginUtil.REQUEST_CODE_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                user = FirebaseAuth.getInstance().currentUser
                binding.cardAskLogin.root.visibility = View.GONE

                userViewModel.getUser()
            } else {
                checkRequirements()
            }
        }
    }

    override fun onBackPressed() {}
}
