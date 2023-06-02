package com.example.athath.presenntation.login_register.onboarding_screens

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.R
import com.example.athath.utils.Constant.SHARED_PREF_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val sharePref: SharedPreferences,
    private val  auth: FirebaseAuth
): ViewModel() {

    //Handle new or old user

    companion object{
        //for opening Shop Activity (old User)
        const val SHOPPING_ACTIVITY = 101
        //for opening Login&Register Activity (New User)
        @SuppressLint("NonConstantResourceId")
        const val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_onBoardingFragment_to_userOptionsFragment

    }

    private val _navigate: MutableStateFlow<Int> = MutableStateFlow(0)
    val navigate = _navigate.asStateFlow()


    init {
        val isStartBtnClicked = sharePref.getBoolean(SHARED_PREF_KEY, false)
        val user = auth.currentUser

        if (user != null){
            viewModelScope.launch { _navigate.emit(SHOPPING_ACTIVITY) }
        } else if (isStartBtnClicked){
            viewModelScope.launch { _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT) }
        }
    }


    fun startBtnClicked() {
        sharePref.edit().putBoolean(SHARED_PREF_KEY, true).apply()
    }








}