package nl.svdoetelaar.capstoneproject.util

import nl.svdoetelaar.capstoneproject.model.GeofenceLocationData

class GeofenceUtil {
    companion object {
        const val TAG = "geofenceUtil"
        var geofenceLocationData: GeofenceLocationData? = null
        const val REQUEST_CODE_GEOFENCE = 0
        const val DAY = 864000000L

    }


}