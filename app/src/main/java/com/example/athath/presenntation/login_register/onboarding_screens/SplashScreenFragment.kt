package com.example.athath.presenntation.login_register.onboarding_screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.athath.R
import com.example.athath.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment: Fragment() {
    private var _binding: SplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashScreenBinding.inflate(inflater)


        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}






