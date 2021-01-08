package nl.svdoetelaar.capstoneproject.model

data class GeofenceLocationData(
    var lat: Double,
    var lng: Double,
    var radius: Double
) {
    companion object {
        const val KEY = "workTimeLoggerGeoFence"
    }
}
