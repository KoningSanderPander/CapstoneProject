package nl.svdoetelaar.capstoneproject.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (!SplashActivity.hasPermissions(applicationContext)) {
            startActivity(
                Intent(
                    this, RequestPermissionsActivity::class.java
                )
            )
        }


    }
}