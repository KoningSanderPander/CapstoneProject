package nl.svdoetelaar.capstoneproject.ui.overview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.databinding.ActivityOverviewBinding

class OverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverviewBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.overview_window_title)
    }
}