package nl.svdoetelaar.capstoneproject.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.model.GeofenceLocationData
import nl.svdoetelaar.capstoneproject.util.LoginUtil
import nl.svdoetelaar.capstoneproject.util.LoginUtil.Companion.user

class GeofenceRepository(private val context: Context) : ParentRepository() {
    private var collection = firestore.collection(context.getString(R.string.geofences))

    private val _geofenceLocationData: MutableLiveData<GeofenceLocationData> = MutableLiveData()
    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val geofenceLocationData: LiveData<GeofenceLocationData> get() = _geofenceLocationData
    val createSuccess: LiveData<Boolean> get() = _createSuccess

    suspend fun getGeoFence() {
        try {
            withTimeout(timeoutMillisDefault) {
                if (user == null) {
                    return@withTimeout
                }
                val result: DocumentSnapshot = collection.document(
                    LoginUtil.user?.uid ?: throw GeofenceRetrievalError("user is not logged in")
                )
                    .get()
                    .await()

                if (result.data == null) {
                    _geofenceLocationData.value = null
                } else {
                    _geofenceLocationData.value = GeofenceLocationData(
                        result.data?.get("lat").toString().toDouble(),
                        result.data?.get("lng").toString().toDouble(),
                        result.data?.get("radius").toString().toDouble(),
                    )
                }
            }
        } catch (e: Exception) {
            throw GeofenceRetrievalError(e.toString())
        }
    }

    suspend fun createGeoFence(geofenceLocationData: GeofenceLocationData) {
        try {
            withTimeout(timeoutMillisDefault) {
                collection.document(
                    LoginUtil.user?.uid ?: throw GeofenceSaveError(
                        "user is not logged in",
                        NullPointerException()
                    )
                )
                    .set(geofenceLocationData)
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