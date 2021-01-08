package nl.svdoetelaar.capstoneproject.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.databinding.ActivityMainBinding
import nl.svdoetelaar.capstoneproject.model.GeofenceLocationData
import nl.svdoetelaar.capstoneproject.ui.main.overview.OverviewFragment
import nl.svdoetelaar.capstoneproject.ui.maps.MapsActivity
import nl.svdoetelaar.capstoneproject.util.GeofenceBroadcastReceiver
import nl.svdoetelaar.capstoneproject.util.GeofenceUtil
import nl.svdoetelaar.capstoneproject.util.GeofenceUtil.Companion.DAY
import nl.svdoetelaar.capstoneproject.util.GeofenceUtil.Companion.geofenceLocationData
import nl.svdoetelaar.capstoneproject.util.LocationService
import nl.svdoetelaar.capstoneproject.util.LoginUtil
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.providers
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.user
import nl.svdoetelaar.capstoneproject.viewmodel.GeofenceViewModel
import nl.svdoetelaar.capstoneproject.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val geofenceViewModel: GeofenceViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var geofencingClient: GeofencingClient

    private val cheapLifecycleOwner = LifecycleOwner { lifecycle }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            GeofenceUtil.REQUEST_CODE_GEOFENCE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        geofencingClient = LocationServices.getGeofencingClient(this)


        binding.bottomNav.selectedItemId = R.id.bottomNavOverview
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
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
        checkRequirements()
        observeGeofenceLocationData()
        geofenceViewModel.getGeofence()
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
        } else {
            if (!LocationService.hasPermissionFineLocation()) {
                binding.cardAskFineLocation.root.visibility = View.VISIBLE
                LocationService.requestFineLocation(this)
            }
            if (!LocationService.hasPermissionBackgroundLocation()) {
                binding.cardAskBackgroundLocation.root.visibility = View.VISIBLE
                LocationService.requestBackgroundLocation(this)
            }
        }
    }

    private fun observeGeofenceLocationData() {
        geofenceViewModel.geofenceLocationData.observeForever {
            geofenceLocationData = it
            checkDeviceLocationSettingsAndStartGeofence()
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
                        binding.cardAskFineLocation.root.visibility = View.GONE
                    } else {
                        binding.cardAskFineLocation.root.visibility = View.VISIBLE
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


    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        if (user == null || geofenceLocationData == null) {
            return
        }
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        this,
                        LocationService.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(
                        GeofenceUtil.TAG,
                        baseContext.getString(
                            R.string.error_getting_location_settings_resolution,
                            sendEx.message
                        )
                    )
                }
            } else {
                Snackbar.make(
                    binding.root,
                    baseContext.getString(R.string.location_services_must_be_enabled),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                addGeofence()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        Log.d("AddGeofence", "making geofence")
        val geofence = Geofence.Builder()
            .setRequestId(GeofenceLocationData.KEY)
            .setCircularRegion(
                geofenceLocationData!!.lat,
                geofenceLocationData!!.lng,
                geofenceLocationData!!.radius.toFloat()
            )
            .setExpirationDuration(DAY)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        Log.d("AddGeofence", "making geofencingRequest")
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        Log.d("AddGeofence", "arrived at weird remove")
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnCompleteListener {
                Log.d("AddGeofence", "succes, making geofence")
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.geofence_added),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            addOnFailureListener {
                Log.d("AddGeofence", "something went wrong while making the geofence")
                Snackbar.make(
                    binding.root,
                    getString(R.string.geofence_not_added),
                    Snackbar.LENGTH_SHORT
                ).show()
                if (it.message != null) {
                    Log.w(GeofenceUtil.TAG, it.message.toString())
                }
            }
        }
    }

    override fun onBackPressed() {}

}
