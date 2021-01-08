package nl.svdoetelaar.capstoneproject.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.model.Geofence
import nl.svdoetelaar.capstoneproject.util.LoginUtil

class GeofenceRepository(private val context: Context) : ParentRepository() {
    private var collection = firestore.collection(context.getString(R.string.geofences))

    private val _geofence: MutableLiveData<Geofence> = MutableLiveData()
    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val geofence: LiveData<Geofence> get() = _geofence
    val createSuccess: LiveData<Boolean> get() = _createSuccess

    suspend fun getGeoFence() {
        try {
            withTimeout(timeoutMillisDefault) {
                val result: DocumentSnapshot = collection.document(
                    LoginUtil.user?.uid ?: throw GeofenceRetrievalError("user is not logged in")
                )
                    .get()
                    .await()

                if (result.data == null) {
                    _geofence.value = null
                } else {
                    _geofence.value = Geofence(
                        result.data?.get("lat").toString().toDouble(),
                        result.data?.get("lng").toString().toDouble(),
                        result.data?.get("radius").toString().toDouble(),
                    )
                }
            }
        } catch (e: Exception) {
            throw GeofenceRetrievalError(context.getString(R.string.retrieval_firebase_unsuccessful))
        }
    }

    suspend fun createGeoFence(geofence: Geofence) {
        try {
            withTimeout(timeoutMillisDefault) {
                collection.document(
                    LoginUtil.user?.uid ?: throw GeofenceSaveError(
                        "user is not logged in",
                        NullPointerException()
                    )
                )
                    .set(geofence)
                    .await()

                _createSuccess.value = true
            }
        } catch (e: Exception) {
            throw GeofenceSaveError(e.message.toString(), e)
        }
    }

    class GeofenceRetrievalError(s: String) : Exception(s)
    class GeofenceSaveError(s: String, cause: Throwable) : Throwable(s, cause)
}