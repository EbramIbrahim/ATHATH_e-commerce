package com.example.athath.presenntation.login_register.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {


    // shared flow handle one time event
    private val _loginState: MutableSharedFlow<Resource<FirebaseUser>> =
        MutableSharedFlow()
    val login = _loginState.asSharedFlow()

    private val _resetPasswordState: MutableSharedFlow<Resource<String>> =
        MutableSharedFlow()
    val resetPassword = _resetPasswordState.asSharedFlow()

    private val _loginValidation = Channel<RegisterLoginFieldState>()
    val loginValidation = _loginValidation.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.emit(Resource.Loading())
            delay(2000L)
            if (checkLoginValidation(email, password)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        result.user?.let {
                            viewModelScope.launch { _loginState.emit(Resource.Success(it)) }
                        }
                    }.addOnFailureListener {
                        viewModelScope.launch { _loginState.emit(Resource.Error(it.message.toString())) }
                    }
            } else {
                val loginValidationState = RegisterLoginFieldState(
                    validateEmail(email),
                    validatePassword(password)
                )
                _loginValidation.send(loginValidationState)
            }

        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordState.emit(Resource.Loading())
            delay(1000L)

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch { _resetPasswordState.emit(Resource.Success(email)) }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _resetPasswordState.emit(
                            Resource.Error(
                                it.message.toString()
                            )
                        )
                    }
                }
        }
    }

    private fun checkLoginValidation(email: String, password: String): Boolean {
        val emailValidate = validateEmail(email)
        val passwordValidate = validatePassword(password)

        return emailValidate is RegisterLoginValidation.Success &&
                passwordValidate is RegisterLoginValidation.Success
    }

}









