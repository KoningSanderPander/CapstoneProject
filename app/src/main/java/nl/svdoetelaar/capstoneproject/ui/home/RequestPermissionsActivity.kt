package nl.svdoetelaar.capstoneproject.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import nl.svdoetelaar.capstoneproject.databinding.ActivityRequestPermissionsBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class RequestPermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestPermissionsBinding

    companion object {
        const val REQUEST_LOCATION_CODE = 10

        val permissions = arrayOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()

        binding.btnRequest.setOnClickListener {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                        binding.cvRequiresPermissions.visibility = View.VISIBLE
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }
        }
    }
}