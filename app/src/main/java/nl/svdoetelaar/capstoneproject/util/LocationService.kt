package nl.svdoetelaar.capstoneproject.util

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import nl.svdoetelaar.capstoneproject.ui.maps.MapsActivity

class LocationService : Service() {

    companion object {
        const val REQUEST_CODE_FINE_LOCATION_PERMISSION = 100
        const val REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION = 101

        const val backgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
        const val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION

        fun hasPermissionFineLocation(): Boolean {
            return ContextCompat.checkSelfPermission(
                MyApplication.appContext!!,
                fineLocation
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasPermissionBackgroundLocation(): Boolean {
            return ContextCompat.checkSelfPermission(
                MyApplication.appContext!!,
                backgroundLocation
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestFineLocation(activity: Activity) {
            activity.requestPermissions(
                arrayOf(fineLocation),
                REQUEST_CODE_FINE_LOCATION_PERMISSION
            )
        }

        fun requestBackgroundLocation(activity: Activity) {
            activity.requestPermissions(
                arrayOf(backgroundLocation),
                REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
