package com.example.athath.presenntation.shopping_screens.address_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.athath.data.models.Address
import com.example.athath.databinding.AddressFragmentBinding
import com.example.athath.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment: Fragment() {
    private var _binding: AddressFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddressFragmentBinding.inflate(layoutInflater)

        binding.textPhone.doOnTextChanged { text, _, _, _ ->
            if (text?.length!! < 10){
                binding.iLPhone.requestFocus()
                binding.iLPhone.error = "Wrong Number!"
            }
            else {
                binding.iLPhone.error = null
            }

        }

        binding.buttonSave.setOnClickListener {
            addNewAddress()
        }

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }
        collectUserAddressState()
        collectInfoValidateState()

        return binding.root
    }


    private fun addNewAddress() {
        val addressTitle = binding.textAddress.text.toString()
        val fullName = binding.textFullName.text.toString()
        val street = binding.textStreet.text.toString()
        val phone = binding.textPhone.text.toString()
        val city = binding.textCity.text.toString()
        val state = binding.textState.text.toString()
        val address = Address(addressTitle, fullName, street, phone, city, state)
        viewModel.addUserAddress(address)
    }

    private fun collectUserAddressState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAddressState.collectLatest { address ->
                    when(address) {
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                "Your location saved successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }
                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                address.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun collectInfoValidateState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.infoValidateState.collectLatest {
                    Toast.makeText(
                        requireContext(),
                        it,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}









