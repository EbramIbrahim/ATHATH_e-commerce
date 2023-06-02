package com.example.athath.presenntation.shopping_screens.orders_screen.order_details_screen

import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import com.example.athath.presenntation.di.OrderNotificationCompatBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(

    @OrderNotificationCompatBuilder
    private val notificationCompat: NotificationCompat.Builder,
    private val notificationManagerCompat: NotificationManagerCompat
) : ViewModel() {


    @SuppressLint("MissingPermission")
    fun showOrderStatusNotification(orderStatus: String, orderId: Long) {
        notificationManagerCompat.notify(
            1,
            notificationCompat.setContentText("Your Order #${orderId} Was $orderStatus")
                .build()
            )
    }


}
















