package nl.svdoetelaar.capstoneproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


class User(
    val firstName: String,
    val lastName: String,
    val hourlyWage: Double,
)