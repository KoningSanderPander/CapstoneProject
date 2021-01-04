package nl.svdoetelaar.capstoneproject.ui.landing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import nl.svdoetelaar.capstoneproject.databinding.ActivitySplashBinding
import nl.svdoetelaar.capstoneproject.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this, MainActivity::class.java
                )
            )
        }, 1_000L)

    }
}

