package com.example.athath.data.models.orders

import android.os.Parcelable
import com.example.athath.data.models.Address
import com.example.athath.data.models.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val ordersId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
):Parcelable {
    constructor():this("", 0f, emptyList(), Address())
}
