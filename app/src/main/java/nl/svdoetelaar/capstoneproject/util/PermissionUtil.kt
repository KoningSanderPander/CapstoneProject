package nl.svdoetelaar.capstoneproject.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtil : MyApplication() {

    companion object {
        const val REQUEST_CODE_LOCATION_PERMISSION = 10

        val permissions = arrayOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        fun hasPermissions(): Boolean {
            var hasPermission = true
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        appContext!!,
                        permission
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    hasPermission = false
                }
            }
            return hasPermission
        }


    }
}