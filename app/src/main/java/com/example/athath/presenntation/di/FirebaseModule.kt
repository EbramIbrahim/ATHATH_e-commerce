package com.example.athath.presenntation.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.athath.utils.Constant.INTRODUCTION_SHARED_PREF
import com.example.athath.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseInstance() = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseFireStoreInstance() = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseStorageInstance() = FirebaseStorage.getInstance().reference


    @Provides
    fun provideSharedPrefInstance(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SHARED_PREF, MODE_PRIVATE)!!

    @Provides
    @Singleton
    fun provideFirebaseUtils(
         firestore: FirebaseFirestore,
         auth: FirebaseAuth
    ): FirebaseUtils {
        return FirebaseUtils(firestore, auth)
    }
}






