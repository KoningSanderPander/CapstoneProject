package nl.svdoetelaar.capstoneproject.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.databinding.ActivityMainBinding
import nl.svdoetelaar.capstoneproject.ui.main.overview.OverviewFragment
import nl.svdoetelaar.capstoneproject.ui.maps.MapsActivity
import nl.svdoetelaar.capstoneproject.util.LocationService
import nl.svdoetelaar.capstoneproject.util.LoginUtil
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.providers
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.user
import nl.svdoetelaar.capstoneproject.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.bottomNav.selectedItemId = R.id.bottomNavOverview
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            Log.d("bottomNav", "item.itemId: ${item.itemId}")
            Log.d("bottomNav", "R.id.bottomNavMap: ${R.id.bottomNavMap}")
            when (item.itemId) {
                R.id.bottomNavMap -> {
                    startActivity(
                        Intent(
                            this,
                            MapsActivity::class.java
                        )
                    )
                    true
                }
                R.id.bottomNavOverview -> {
                    loadFragment(OverviewFragment())
                    true
                }
                R.id.bottomNavUser -> {
                    loadFragment(UserInfoFragment())
                    true
                }
                else -> false
            }
        }

        if (user == null) {
            startLoginActivity()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.nav_host_fragment, fragment)
        ft.commit()
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
            LocationService.REQUEST_CODE_FINE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (LocationService.hasPermissionFineLocation()) {
                        binding.cardAskForegroundLocation.root.visibility = View.GONE
                    } else {
                        binding.cardAskForegroundLocation.root.visibility = View.VISIBLE
                    }
                }
            }
            LocationService.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (LocationService.hasPermissionBackgroundLocation()) {
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
