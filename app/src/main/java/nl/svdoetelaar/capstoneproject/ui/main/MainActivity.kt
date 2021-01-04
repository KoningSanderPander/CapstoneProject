package nl.svdoetelaar.capstoneproject.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        checkRequirements()
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
        if (!PermissionUtil.hasPermissions()) {
            binding.cardAskLocation.root.visibility = View.VISIBLE
            binding.cardAskLocation.btnRequest.setOnClickListener {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            PermissionUtil.permissions,
            PermissionUtil.REQUEST_CODE_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (PermissionUtil.hasPermissions()) {
                        binding.cardAskLocation.root.visibility = View.GONE
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
            }
        }
    }

    override fun onBackPressed() {}
}
