package com.example.athath.presenntation.login_register.register_screen

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
import com.example.athath.data.models.User
import com.example.athath.databinding.FragmentRegisterBinding
import com.example.athath.utils.RegisterLoginValidation
import com.example.athath.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        binding.loginTv.setOnClickListener {
            findNavController()
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerBtn.setOnClickListener {
            registerNewUser()
        }
        collectNewUserData()
        collectRegisterFieldState()

        return binding.root
    }


    private fun registerNewUser() {
        val user = User(
            binding.firstNameEt.text.toString().trim(),
            binding.lastNameEt.text.toString().trim(),
            binding.emailEt.text.toString().trim()
        )
        val password = binding.passwordEt.text.toString()
        viewModel.createAccountWithEmailAndPassword(user, password)

    }

    private fun collectNewUserData() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerUserState.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            binding.registerBtn.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.registerBtn.revertAnimation()
                            findNavController()
                                .navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is Resource.Error -> {
                            binding.registerBtn.revertAnimation()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun collectRegisterFieldState(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.validation.collectLatest { validation ->
                    if (validation.email is RegisterLoginValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.emailEt.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                            binding.registerBtn.revertAnimation()
                        }
                    }
                    if (validation.password is RegisterLoginValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.passwordEt.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                            binding.registerBtn.revertAnimation()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}