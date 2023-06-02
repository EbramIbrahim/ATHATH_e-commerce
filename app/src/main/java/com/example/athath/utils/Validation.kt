package com.example.athath.utils

import android.util.Patterns

fun validateEmail(email: String): RegisterLoginValidation {
    if (email.isEmpty())
        return RegisterLoginValidation.Failed("Email cannot be empty...")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterLoginValidation.Failed("wrong email format...")

    return RegisterLoginValidation.Success

}

fun validatePassword(password: String): RegisterLoginValidation {
    if (password.isEmpty())
        return RegisterLoginValidation.Failed("Password cannot be empty...")

    if (password.length < 6)
        return RegisterLoginValidation.Failed("Password should contain 6 chars")


    return RegisterLoginValidation.Success
}







