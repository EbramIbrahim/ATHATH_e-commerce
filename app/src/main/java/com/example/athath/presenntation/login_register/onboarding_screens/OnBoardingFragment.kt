package com.example.athath.presenntation.login_register.onboarding_screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.athath.R
import com.example.athath.ShoppingActivity
import com.example.athath.databinding.FragmentOnboardingBinding
import com.example.athath.presenntation.login_register.onboarding_screens.OnBoardingViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.athath.presenntation.login_register.onboarding_screens.OnBoardingViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingFragment: Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<OnBoardingViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.navigate.collect{ value ->
                    when(value) {
                         SHOPPING_ACTIVITY -> {
                             Intent(requireActivity(), ShoppingActivity::class.java).also {
                                 it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                 startActivity(it)
                             }
                         }
                        ACCOUNT_OPTIONS_FRAGMENT -> {
                            findNavController().navigate(value)
                        }
                    }
                }
            }
        }


        binding.startBtn.setOnClickListener {
            viewModel.startBtnClicked()
            findNavController()
                .navigate(R.id.action_onBoardingFragment_to_userOptionsFragment)
        }

        return binding.root

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}