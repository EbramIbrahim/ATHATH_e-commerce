package com.example.athath.presenntation.login_register.user_account_screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.data.models.User
import com.example.athath.databinding.FragmentUserAccountBinding
import com.example.athath.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment: Fragment() {
    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imgActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imgUri: Uri? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAccountBinding.inflate(inflater)

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }
        imgActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
                if (result.resultCode == RESULT_OK){
                    imgUri = result.data?.data
                    Glide.with(this).load(imgUri).into(binding.imageUser)
                }


            }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collectLatest {user ->
                    when(user) {
                        is Resource.Loading -> {
                            showUserLoading()
                        }
                        is Resource.Success -> {
                            hideUserLoading()
                            showUserInfo(user.data)
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                user.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString()
                val lastName = edLastName.text.toString()
                val email = edEmail.text.toString()

                val user = User(firstName, lastName, email)
                viewModel.updateUserData(user, imgUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.type = "image/jpg"
            imgActivityResultLauncher.launch(intent)
        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateUserInfo.collectLatest { user ->
                    when(user) {
                        is Resource.Loading -> {
                            binding.buttonSave.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.buttonSave.revertAnimation()
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                user.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

    }

    private fun showUserInfo(data: User?) {
        binding.apply {
            Glide.with(this@UserAccountFragment)
                .load(data?.imagePath)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(ColorDrawable(Color.BLACK))
                .into(imageUser)
            edFirstName.setText(data?.firstName)
            edLastName.setText(data?.lastName)
            edEmail.setText(data?.email)
        }
    }


    private fun showUserLoading(){
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
        }
    }

    private fun hideUserLoading(){
        binding.apply {
            progressbarAccount.visibility = View.INVISIBLE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}










