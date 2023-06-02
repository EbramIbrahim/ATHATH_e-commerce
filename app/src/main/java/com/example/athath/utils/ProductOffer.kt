package com.example.athath.utils

fun Float?.offer(price: Float): Float {
    if (this == null)
        return price

    val offerPercentage = 1 - this
    return offerPercentage * price
}