package nl.svdoetelaar.capstoneproject.repositories

import com.google.firebase.firestore.FirebaseFirestore
import nl.svdoetelaar.capstoneproject.util.MyApplication

abstract class ParentRepository {
    protected var firestore = FirebaseFirestore.getInstance()
    protected val timeoutMillisDefault = 5_000L
}