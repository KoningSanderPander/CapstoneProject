package nl.svdoetelaar.capstoneproject.util

import android.content.pm.PackageManager

object PermissionUtils {

    @JvmStatic
    fun isPermissionGranted(
        permission: String,
        permissions: Array<out String>,
        grantResults: IntArray,
    ): Boolean {
        for (i in permissions.indices) {
            if (permissions[i] == permission) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

}