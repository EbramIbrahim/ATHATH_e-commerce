package com.example.athath.presenntation.login_register.login_screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.athath.R
import com.example.athath.ShoppingActivity
import com.example.athath.databinding.FragmentLoginBinding
import com.example.athath.utils.RegisterLoginValidation
import com.example.athath.utils.Resource
import com.example.athath.utils.setUpBottomDialogSheet
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        binding.registerTv.setOnClickListener {
            findNavController()
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgetPasswordTv.setOnClickListener {
            setUpBottomDialogSheet { email ->
                viewModel.resetPassword(email)
            }
        }
        collectResetPasswordState()

        binding.loginBtn.setOnClickListener {
            loginUser()
        }

        collectUserState()
        collectLoginFieldState()

        return binding.root
    }


    private fun loginUser() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString()
        viewModel.login(email, password)
    }

    private fun collectUserState(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.login.collectLatest { value ->
                    when(value){
                        is Resource.Loading -> {
                            binding.loginBtn.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.loginBtn.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Login Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            Intent(requireActivity(), ShoppingActivity::class.java).also {
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(it)
                            }
                        }
                        is Resource.Error -> {
                            binding.loginBtn.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                value.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }


    private fun collectResetPasswordState(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.resetPassword.collectLatest { value ->
                    when(value){
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            Snackbar.make(
                                requireView(),
                                "Reset Password email sent successfully",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(
                                requireView(),
                                value.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun collectLoginFieldState(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginValidation.collectLatest { validation ->
                    if (validation.email is RegisterLoginValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.emailEt.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                            binding.loginBtn.revertAnimation()
                        }
                    }
                    if (validation.password is RegisterLoginValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.passwordEt.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                            binding.loginBtn.revertAnimation()
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