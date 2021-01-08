package nl.svdoetelaar.capstoneproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.svdoetelaar.capstoneproject.model.Geofence
import nl.svdoetelaar.capstoneproject.model.User
import nl.svdoetelaar.capstoneproject.repositories.GeofenceRepository
import nl.svdoetelaar.capstoneproject.repositories.UserRepository

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "FIREBASE_USER"
    private val repository = GeofenceRepository(application.applicationContext)
    val geofence = repository.geofence
    val createSuccess = repository.createSuccess

    fun getGeofence() {
        viewModelScope.launch {
            try {
                repository.getGeoFence()
            } catch (e: UserRepository.UserRetrievalError) {
                val errorMsg = "Something went wrong while saving profile"
                Log.e(tag, e.message ?: errorMsg)
            }
        }
    }

    fun createGeofence(geofence: Geofence) {
        viewModelScope.launch {
            try {
                repository.createGeoFence(geofence)
            } catch (e: UserRepository.UserSaveError) {
                val errorMsg = "Something went wrong while saving profile"
                Log.e(tag, e.message ?: errorMsg)
            }
        }
    }
}