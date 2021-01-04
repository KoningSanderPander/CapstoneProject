package nl.svdoetelaar.capstoneproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.svdoetelaar.capstoneproject.model.User
import nl.svdoetelaar.capstoneproject.repositories.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "FIREBASE_USER"
    private val repository = UserRepository(application.applicationContext)
    val user = repository.user
    val createSuccess = repository.createSuccess

    fun getUser() {
        viewModelScope.launch {
            try {
                repository.getUser()
            } catch (e: UserRepository.UserRetrievalError) {
                val errorMsg = "Something went wrong while saving profile"
                Log.e(tag, e.message ?: errorMsg)
            }
        }
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            try {
                repository.createUser(user)
            } catch (e: UserRepository.UserSaveError) {
                val errorMsg = "Something went wrong while saving profile"
                Log.e(tag, e.message ?: errorMsg)
            }
        }
    }
}