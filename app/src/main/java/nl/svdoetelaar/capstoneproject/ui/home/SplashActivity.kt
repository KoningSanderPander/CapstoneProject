package nl.svdoetelaar.capstoneproject.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import nl.svdoetelaar.capstoneproject.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    companion object {
        fun hasPermissions(context: Context): Boolean {
            for (permission in RequestPermissionsActivity.permissions) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    return false
                }
            }
            return true
        }
    }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (hasPermissions(applicationContext)) {

            Snackbar.make(binding.root, "has permission", Snackbar.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({

                startActivity(
                    Intent(
                        this, MainActivity::class.java
                    )
                )
            }, 1_000L)

        } else {

            Snackbar.make(binding.root, "no permission", Snackbar.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({

                startActivity(
                    Intent(
                        this, RequestPermissionsActivity::class.java
                    )
                )
            }, 1_000L)

        }


    }

}