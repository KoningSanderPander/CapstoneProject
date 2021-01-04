package nl.svdoetelaar.capstoneproject.util

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser

class LoginUtil {
    companion object {
        const val REQUEST_CODE_SIGN_IN = 123
        var user: FirebaseUser? = null
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
        )
    }
}