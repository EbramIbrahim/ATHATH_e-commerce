package com.example.athath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
):Parcelable {
    constructor(): this(Product(), 0,0,"")
}