package com.example.athath.presenntation.login_register.register_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.User
import com.example.athath.utils.*
import com.example.athath.utils.Constant.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore
) : ViewModel() {

    private val _registerUserState: MutableStateFlow<Resource<User>> =
        MutableStateFlow(Resource.UnSpecified())
    val registerUserState = _registerUserState.asStateFlow()

    private val _validation = Channel<RegisterLoginFieldState>()
    val validation = _validation.receiveAsFlow()


    fun createAccountWithEmailAndPassword(user: User, password: String) {
        viewModelScope.launch {
            _registerUserState.emit(Resource.Loading())
            delay(2000L)
            if (checkEmailAndPasswordValidation(user, password)) {
                firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                    .addOnSuccessListener { authResult ->
                        authResult.user?.let {
                            saveUserDate(it.uid, user)
                        }
                    }
                    .addOnFailureListener {
                        _registerUserState.value = Resource.Error(
                            it.message
                        )
                    }
            } else {
                val registerFieldState = RegisterLoginFieldState(
                    validateEmail(user.email),
                    validatePassword(password)
                )
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveUserDate(userUid: String, user: User){
        database.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _registerUserState.value = Resource.Success(user)
            }.addOnFailureListener{
                _registerUserState.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkEmailAndPasswordValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return emailValidation is RegisterLoginValidation.Success &&
                passwordValidation is RegisterLoginValidation.Success
    }


}