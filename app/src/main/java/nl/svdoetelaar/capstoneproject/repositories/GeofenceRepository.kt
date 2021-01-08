package nl.svdoetelaar.capstoneproject.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.model.User
import nl.svdoetelaar.capstoneproject.util.LoginUtil

class GeofenceRepository(private val context: Context) : ParentRepository() {
    private var collection = firestore.collection("users")

    private val _user: MutableLiveData<User> = MutableLiveData()
    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val user: LiveData<User> get() = _user
    val createSuccess: LiveData<Boolean> get() = _createSuccess

    suspend fun getUser() {
        try {
            withTimeout(timeoutMillisDefault) {
                val result : DocumentSnapshot = collection.document(LoginUtil.user!!.uid)
                    .get()
                    .await()

                if (result.data == null) {
                    _user.value = null
                } else {
                    _user.value = User(
                        result.data?.get("firstName").toString(),
                        result.data?.get("firstName").toString(),
                        result.data?.get("hourlyWage").toString().toDouble()
                    )
                }
            }
        } catch (e: Exception) {
            throw UserRetrievalError(context.getString(R.string.retrieval_firebase_unsuccessful))
        }
    }

    suspend fun createUser(user: User) {
        try {
            withTimeout(timeoutMillisDefault) {
                collection.document(LoginUtil.user!!.uid)
                    .set(user)
                    .await()

                _createSuccess.value = true
            }
        } catch (e: Exception) {
            throw UserSaveError(e.message.toString(), e)
        }
    }

    class UserRetrievalError(s: String) : Exception(s)
    class UserSaveError(s: String, cause: Throwable) : Throwable(s, cause)
}