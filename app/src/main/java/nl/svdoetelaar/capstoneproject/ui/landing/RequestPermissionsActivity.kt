package nl.svdoetelaar.capstoneproject.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import nl.svdoetelaar.capstoneproject.databinding.ActivityRequestPermissionsBinding
import nl.svdoetelaar.capstoneproject.util.PermissionUtil.Companion.REQUEST_CODE_LOCATION_PERMISSION
import nl.svdoetelaar.capstoneproject.util.PermissionUtil.Companion.permissions

class RequestPermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestPermissionsBinding


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
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }
    }
}