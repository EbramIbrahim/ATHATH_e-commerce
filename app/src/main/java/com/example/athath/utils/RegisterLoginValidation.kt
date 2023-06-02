package com.example.athath.utils

sealed class RegisterLoginValidation {
    object Success: RegisterLoginValidation()
    data class Failed(val message: String): RegisterLoginValidation()
}


data class RegisterLoginFieldState(
    val email: RegisterLoginValidation, //<-- Has Success and Failed State
    val password: RegisterLoginValidation //<-- Has Success and Failed State
)