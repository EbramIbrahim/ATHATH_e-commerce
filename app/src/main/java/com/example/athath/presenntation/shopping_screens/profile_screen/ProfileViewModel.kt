package com.example.athath.presenntation.shopping_screens.profile_screen



import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.athath.data.models.User
import com.example.athath.presenntation.di.MainNotificationCompatBuilder
import com.example.athath.utils.Constant
import com.example.athath.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

    @MainNotificationCompatBuilder
    private val notificationCompat: NotificationCompat.Builder,
    private val notificationManagerCompat: NotificationManagerCompat
) : ViewModel() {

    private val _user: MutableStateFlow<Resource<User>> =
        MutableStateFlow(Resource.UnSpecified())
    val user = _user.asStateFlow()



    init {
        getUser()
    }


    private fun getUser() {

        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                viewModelScope.launch {
                    _user.emit(Resource.Loading())
                    delay(Constant.DELAY_DURATION)
                }
                if (error != null || value == null) {
                    viewModelScope.launch { _user.emit(Resource.Error(error?.message)) }
                } else {
                    val user = value.toObject(User::class.java)
                    viewModelScope.launch { _user.emit(Resource.Success(user)) }
                }
            }
    }



   @SuppressLint("MissingPermission")
    fun showNotification() {
        notificationManagerCompat.notify(1, notificationCompat.build())
    }

    fun cancelSimpleNotification() {
        notificationManagerCompat.cancel(1)
    }

    fun signOut() {
        auth.signOut()
    }


}







