package com.example.athath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
     val address: String,
     val fullName: String,
     val street: String,
     val phone: String,
     val city: String,
     val state: String,
):Parcelable {
    constructor():this("","","","","","")
}