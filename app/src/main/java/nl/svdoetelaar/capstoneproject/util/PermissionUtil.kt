package nl.svdoetelaar.capstoneproject.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtil : MyApplication() {

    companion object {
        const val REQUEST_CODE_FOREGROND_LOCATION_PERMISSION = 100
        const val REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION = 101

        const val backgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
        const val foregroundLocation = Manifest.permission.ACCESS_FINE_LOCATION


        fun hasPermissionForegroundLocation(): Boolean {
            return ContextCompat.checkSelfPermission(
                appContext!!,
                foregroundLocation
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasPermissionBackgroundLocation(): Boolean {
            return ContextCompat.checkSelfPermission(
                appContext!!,
                backgroundLocation
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestForegroundLocation(activity: Activity) {
            activity.requestPermissions(
                arrayOf(foregroundLocation),
                REQUEST_CODE_FOREGROND_LOCATION_PERMISSION
            )

        }

        fun requestBackgroundLocation(activity: Activity) {
            activity.requestPermissions(
                arrayOf(backgroundLocation),
                REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION
            )
        }


    }
}