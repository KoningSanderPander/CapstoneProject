package nl.svdoetelaar.capstoneproject.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "geofenceBroadcast"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        Log.d("geofenceBroadcast", "onReceive")

        val geofenceTransition = geofencingEvent.geofenceTransition

        Log.d("geofenceBroadcast", "original: ${geofenceTransition}")
        Log.d("geofenceBroadcast", "enter: ${Geofence.GEOFENCE_TRANSITION_ENTER}")
        Log.d("geofenceBroadcast", "exit: ${Geofence.GEOFENCE_TRANSITION_EXIT}")


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            val triggeringGeofences = geofencingEvent.triggeringGeofences


        }
    }
}