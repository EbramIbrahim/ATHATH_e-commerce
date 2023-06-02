package com.example.athath.presenntation.shopping_screens.profile_screen

import android.annotation.SuppressLint

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.BuildConfig
import com.example.athath.LoginRegisterActivity
import com.example.athath.R
import com.example.athath.data.models.User
import com.example.athath.databinding.ProfileFragmentBinding
import com.example.athath.utils.Resource
import com.example.athath.utils.showBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater)
        collectUserInfo()




        binding.apply {
            constraintProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
            }
            linearAllOrders.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            }

            linearBilling.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                    0f, emptyArray()
                )
                findNavController().navigate(action)
            }

            linearLogOut.setOnClickListener {
                viewModel.signOut()
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            switchNotification.setOnCheckedChangeListener{ buttonView, isChecked ->
                if (isChecked)
                    viewModel.showNotification()
                else
                    viewModel.cancelSimpleNotification()
            }


        }




        return binding.root
    }


    private fun collectUserInfo() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collectLatest { user ->
                    when (user) {
                        is Resource.Loading -> {
                            binding.progressbarSettings.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarSettings.visibility = View.INVISIBLE
                            userInfo(user.data)
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

    @SuppressLint("SetTextI18n")
    private fun userInfo(user: User?) {
        binding.apply {
            Glide.with(this@ProfileFragment)
                .load(user?.imagePath)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(ColorDrawable(Color.BLACK))
                .into(imageUser)

            tvUserName.text = "${user?.firstName} ${user?.lastName}"

            tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"
        }
    }




    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

