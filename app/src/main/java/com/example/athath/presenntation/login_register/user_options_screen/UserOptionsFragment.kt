package com.example.athath.presenntation.login_register.user_options_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.athath.R
import com.example.athath.databinding.FragmentAccountOptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserOptionsFragment: Fragment() {
    private var _binding: FragmentAccountOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountOptionsBinding.inflate(layoutInflater, container, false)

        binding.registerBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_userOptionsFragment_to_registerFragment)
        }
        binding.loginBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_userOptionsFragment_to_loginFragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}