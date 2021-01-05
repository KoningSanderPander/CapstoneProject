package nl.svdoetelaar.capstoneproject.util

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationsService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
