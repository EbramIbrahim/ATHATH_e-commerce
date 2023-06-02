package com.example.athath.presenntation.login_register.user_account_screen

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.AthathApplication
import com.example.athath.data.models.User
import com.example.athath.utils.Constant.DELAY_DURATION
import com.example.athath.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {


    private val _user: MutableStateFlow<Resource<User>> =
        MutableStateFlow(Resource.UnSpecified())
    val user = _user.asStateFlow()

    private val _updateUserInfo: MutableStateFlow<Resource<User>> =
        MutableStateFlow(Resource.UnSpecified())
    val updateUserInfo = _updateUserInfo.asStateFlow()


    init {
        getUser()
    }


    private fun getUser() {
        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                viewModelScope.launch {
                    _user.emit(Resource.Loading())
                    delay(DELAY_DURATION)
                }
                if (error != null || value == null) {
                    viewModelScope.launch { _user.emit(Resource.Error(error?.message)) }
                } else {
                    val user = value.toObject(User::class.java)
                    viewModelScope.launch { _user.emit(Resource.Success(user)) }
                }
            }
    }


    fun updateUserData(user: User, imgUri: Uri?) {
        if (imgUri == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImg(user, imgUri)
        }
    }


    private fun saveUserInformationWithNewImg(user: User, imgUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _updateUserInfo.emit(Resource.Loading())
                delay(4000L)
            }
            try {
                val imgBitMap = MediaStore.Images.Media.getBitmap(
                    getApplication<AthathApplication>().contentResolver,
                    imgUri
                )
                val byteArrayStream = ByteArrayOutputStream()
                imgBitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
                val imageByteArray = byteArrayStream.toByteArray()
                val imageDirectory =
                    storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)

            } catch (e: Exception) {
                viewModelScope.launch { _updateUserInfo.emit(Resource.Error(e.message.toString())) }

            }
        }
    }


    private fun saveUserInformation(user: User, shouldRetrieveOldImg: Boolean) {

        firestore.runTransaction { transaction ->
            // 1- get the document that i should work on
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrieveOldImg) {
                viewModelScope.launch {
                    _updateUserInfo.emit(Resource.Loading())
                    delay(4000L)
                }
                // 2- get the document data
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                // 3- set the new data
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch { _updateUserInfo.emit(Resource.Success(user)) }
        }.addOnFailureListener {
            viewModelScope.launch { _updateUserInfo.emit(Resource.Error(it.message.toString())) }
        }
    }





}