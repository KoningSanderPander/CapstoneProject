package nl.svdoetelaar.capstoneproject.model

import com.google.type.LatLng

data class GeoFence (
    var centre: LatLng,
    var radius: Double
        )
