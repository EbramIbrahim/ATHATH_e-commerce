package com.example.athath.presenntation.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.athath.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {


    @Provides
    @Singleton
    @MainNotificationCompatBuilder
    fun provideNotificationCompat(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "Simple Channel Id")
            .setContentTitle("ATHATH")
            .setContentText("Notifications are Activated")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    @Provides
    @Singleton
    @OrderNotificationCompatBuilder
    fun provideOrderStatusNotification(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "Order Status Channel")
            .setSmallIcon(R.drawable.ic_shipping)
            .setContentTitle("Order Status")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerCompat {
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Simple Channel Id",
                "Simple Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val orderChannel = NotificationChannel(
                "Order Status Channel",
                "Order Status",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManagerCompat.createNotificationChannel(channel)
            notificationManagerCompat.createNotificationChannel(orderChannel)
        }
        return notificationManagerCompat
    }





}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainNotificationCompatBuilder



@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OrderNotificationCompatBuilder








