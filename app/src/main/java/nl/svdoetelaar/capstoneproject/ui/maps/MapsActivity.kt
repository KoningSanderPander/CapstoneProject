package nl.svdoetelaar.capstoneproject.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.slider.Slider
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.databinding.ActivityMapsBinding
import nl.svdoetelaar.capstoneproject.model.Geofence
import nl.svdoetelaar.capstoneproject.ui.main.MainActivity
import nl.svdoetelaar.capstoneproject.util.LocationService
import nl.svdoetelaar.capstoneproject.util.PermissionUtils
import nl.svdoetelaar.capstoneproject.viewmodel.GeofenceViewModel
import java.util.*

class MapsActivity : AppCompatActivity(),
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener,
    OnMapReadyCallback,
    LocationListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private val geofenceViewModel: GeofenceViewModel by viewModels()

    companion object {
        private const val minTime = 50L
        private const val minDistance = 1000f
    }

    private lateinit var binding: ActivityMapsBinding

    private var permissionDenied = false
    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var locationManager: LocationManager

    // Variables for inner draggable circle
    private val circles = ArrayList<DraggableCircle>(1)

    // Color defaults
    private val fillHueProgress = 210
    private val fillAlpha = 0.25
    private val strokeWidth = 0.2
    private val strokeHueProgress = 230
    private val strokeAlpha = 0.5

    // Other settings
    private val maxWidthPx = 50
    private val maxHueDegree = 360

    private val maxAlpha = 255
    private val patternDashLength = 25
    private val patternGapLength = 50

    private val minCircleRadius = 10.0f
    private val maxCircleRadius = 500.0f
    private var defaultCircleRadius = 50.0f

    private val dot = Dot()
    private val dash = Dash(patternDashLength.toFloat())
    private val gap = Gap(patternGapLength.toFloat())
    private val patternDotted = listOf(dot, gap)
    private val patternDashed = listOf(dash, gap)
    private val patternMixed = listOf(dot, gap, dot, dash, gap)

    // These are the options for stroke patterns
    private val patterns: List<Pair<Int, List<PatternItem>?>> = listOf(
        Pair(R.string.pattern_solid, null),
        Pair(R.string.pattern_dashed, patternDashed),
        Pair(R.string.pattern_dotted, patternDotted),
        Pair(R.string.pattern_mixed, patternMixed)
    )

    private var fillColorArgb: Int = 0
    private var strokeColorArgb: Int = 0
    private var circleRadiusMeters: Double = 50.0

    private lateinit var fillHueBar: SeekBar
    private lateinit var fillAlphaBar: SeekBar
    private lateinit var strokeWidthBar: SeekBar
    private lateinit var strokeHueBar: SeekBar
    private lateinit var strokeAlphaBar: SeekBar
    private lateinit var strokePatternSpinner: Spinner
    private lateinit var clickabilityCheckbox: CheckBox
    private lateinit var radiusSlider: Slider

    private inner class DraggableCircle(
        private var circleCenter: LatLng,
    ) {
        private val centerMarker: Marker = map.addMarker(MarkerOptions().apply {
            position(circleCenter)
            draggable(true)
        })

        private val circle: Circle = map.addCircle(
            CircleOptions().apply {
                center(circleCenter)
                radius(circleRadiusMeters)
                strokeWidth(strokeWidthBar.progress.toFloat())
                strokeColor(strokeColorArgb)
                fillColor(fillColorArgb)
                clickable(clickabilityCheckbox.isChecked)
                strokePattern(getSelectedPattern(strokePatternSpinner.selectedItemPosition))
            })

        fun onMarkerMoved(marker: Marker): Boolean {
            when (marker) {
                centerMarker -> {
                    circle.center = marker.position
                }
                else -> return false
            }
            return true
        }

        fun getCenter(): LatLng {
            return circle.center
        }

        fun onCircleMoved(center: LatLng) {
            this.circleCenter = center
            updateCircleAndMarker()
        }

        private fun updateCircleAndMarker() {
            circle.center = circleCenter
            centerMarker.position = circleCenter
        }

        fun onStyleChange() {
            with(circle) {
                strokeWidth = strokeWidthBar.progress.toFloat()
                strokeColor = strokeColorArgb
                fillColor = fillColorArgb
                radius = circleRadiusMeters
            }
        }

        fun setStrokePattern(pattern: List<PatternItem>?) {
            circle.strokePattern = pattern
        }

        fun setClickable(clickable: Boolean) {
            circle.isClickable = clickable
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fillHueBar = binding.fillHueSeekBar.apply {
            max = maxHueDegree
            progress = fillHueProgress
        }
        fillAlphaBar = binding.fillAlphaSeekBar.apply {
            max = maxAlpha
            progress = (maxAlpha * fillAlpha).toInt()
        }
        strokeWidthBar = binding.strokeWidthSeekBar.apply {
            max = maxWidthPx
            progress = (maxWidthPx * strokeWidth).toInt()
        }
        strokeHueBar = binding.strokeHueSeekBar.apply {
            max = maxHueDegree
            progress = strokeHueProgress
        }
        strokeAlphaBar = binding.strokeAlphaSeekBar.apply {
            max = maxAlpha
            progress = (maxAlpha * strokeAlpha).toInt()
        }
        radiusSlider = binding.radiusSlider.apply {
            valueFrom = minCircleRadius
            value = defaultCircleRadius
            valueTo = maxCircleRadius
        }

        strokePatternSpinner = binding.strokePatternSpinner.apply {
            adapter = ArrayAdapter(
                this@MapsActivity,
                android.R.layout.simple_spinner_item,
                getResourceStrings()
            )
        }

        clickabilityCheckbox = findViewById(R.id.toggleClickability)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.bottomNavMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        binding.btnConfirmWorkLocation.setOnClickListener {
            if (!LocationService.hasPermissionBackgroundLocation()) {
                LocationService.requestBackgroundLocation(this)
            }
            geofenceViewModel.createGeofence(
                with(circles[0]) {
                    Geofence(
                        getCenter().latitude,
                        getCenter().longitude,
                        circleRadiusMeters
                    )
                }
            )
        }

        // If it works, it works. No questions from the audience please.
        geofenceViewModel.createSuccess.observe(mapFragment.viewLifecycleOwner, {
            if (it) {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            }
        })
        geofenceViewModel.getGeofence()

    }

    private fun getResourceStrings() = (patterns).map { getString(it.first) }.toTypedArray()

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        with(map) {
            setOnMyLocationClickListener(this@MapsActivity)
            setOnMyLocationButtonClickListener(this@MapsActivity)

            enableMyLocation()

            setOnMapLongClickListener { point ->
                if (circles.size == 0) {
                    circles.add(DraggableCircle(point))
                } else {
                    circles[0].onCircleMoved(point)
                }
            }

            setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker?) {
                    if (marker != null) {
                        onMarkerMoved(marker)
                    }
                }

                override fun onMarkerDrag(marker: Marker?) {
                    if (marker != null) {
                        onMarkerMoved(marker)
                    }
                }

                override fun onMarkerDragEnd(marker: Marker?) {
                    if (marker != null) {
                        onMarkerMoved(marker)
                    }
                }
            })

            setOnCircleClickListener { c -> c.strokeColor = c.strokeColor xor 0x00ffffff }

        }

        fillColorArgb = Color.HSVToColor(
            fillAlphaBar.progress,
            floatArrayOf(fillHueBar.progress.toFloat(), 1f, 1f)
        )
        strokeColorArgb = Color.HSVToColor(
            strokeAlphaBar.progress,
            floatArrayOf(strokeHueBar.progress.toFloat(), 1f, 1f)
        )

        // Set listeners for all the SeekBar
        fillHueBar.setOnSeekBarChangeListener(this)
        fillAlphaBar.setOnSeekBarChangeListener(this)

        strokeWidthBar.setOnSeekBarChangeListener(this)
        strokeHueBar.setOnSeekBarChangeListener(this)
        strokeAlphaBar.setOnSeekBarChangeListener(this)

        radiusSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                when (slider) {
                    radiusSlider -> {
                        circleRadiusMeters = radiusSlider.value.toDouble()
                        circles.map { draggableCircle -> draggableCircle.onStyleChange() }

                    }
                }
            }

            override fun onStopTrackingTouch(slider: Slider) {
                when (slider) {
                    radiusSlider -> {
                        circleRadiusMeters = radiusSlider.value.toDouble()
                        circles.map { draggableCircle -> draggableCircle.onStyleChange() }
                    }
                }
            }

        })

        strokePatternSpinner.onItemSelectedListener = this
    }

    private fun getSelectedPattern(pos: Int): List<PatternItem>? = patterns[pos].second

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (parent.id == R.id.strokePatternSpinner) {
            circles.map { it.setStrokePattern(getSelectedPattern(pos)) }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Don't do anything here.
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // Don't do anything here.
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // Don't do anything here.
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        // Update the fillColorArgb if the SeekBars for it is changed, otherwise keep the old value
        fillColorArgb = when (seekBar) {
            fillHueBar -> Color.HSVToColor(
                Color.alpha(fillColorArgb),
                floatArrayOf(progress.toFloat(), 1f, 1f)
            )
            fillAlphaBar -> Color.argb(
                progress, Color.red(fillColorArgb),
                Color.green(fillColorArgb), Color.blue(fillColorArgb)
            )
            else -> fillColorArgb
        }

        // Set the strokeColorArgb if the SeekBars for it is changed, otherwise keep the old value
        strokeColorArgb = when (seekBar) {
            strokeHueBar -> Color.HSVToColor(
                Color.alpha(strokeColorArgb),
                floatArrayOf(progress.toFloat(), 1f, 1f)
            )
            strokeAlphaBar -> Color.argb(
                progress, Color.red(strokeColorArgb),
                Color.green(strokeColorArgb), Color.blue(strokeColorArgb)
            )
            else -> strokeColorArgb
        }

        // Apply the style change to all the circles.
        circles.map { it.onStyleChange() }
    }

    private fun onMarkerMoved(marker: Marker) {
        circles.forEach { if (it.onMarkerMoved(marker)) return }
    }

    /** Listener for the Clickable CheckBox, to set if all the circles can be click */
    fun toggleClickability(view: View) {
        circles.map { it.setClickable((view as CheckBox).isChecked) }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return

        if (LocationService.hasPermissionFineLocation()) {
            map.isMyLocationEnabled = true

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                this
            )
        } else {
            LocationService.requestFineLocation(this)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ),
                15.0f
            )
        )
        locationManager.removeUpdates(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LocationService.REQUEST_CODE_FINE_LOCATION_PERMISSION -> {
                if (PermissionUtils.isPermissionGranted(
                        LocationService.fineLocation,
                        permissions,
                        grantResults
                    )
                ) {
                    enableMyLocation()
                } else {
                    permissionDenied = true
                }
            }
        }
    }
}