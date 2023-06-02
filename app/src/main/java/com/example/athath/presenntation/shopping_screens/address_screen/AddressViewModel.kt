package com.example.athath.presenntation.shopping_screens.address_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.Address
import com.example.athath.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {


    private val _userAddressState: MutableStateFlow<Resource<Address>> =
        MutableStateFlow(Resource.UnSpecified())
    val userAddressState = _userAddressState.asStateFlow()

    private val _infoValidateState: MutableSharedFlow<String> = MutableSharedFlow()
    val infoValidateState = _infoValidateState.asSharedFlow()


    fun addUserAddress(address: Address) {
        if (checkAddressValidation(address)) {
            viewModelScope.launch {
                _userAddressState.emit(Resource.Loading())
                delay(2000L)
            }
            firestore.collection("user").document(auth.uid!!).collection("address")
                .document().set(address)
                .addOnSuccessListener {

                    viewModelScope.launch { _userAddressState.emit(Resource.Success(address)) }

                }.addOnFailureListener {
                    viewModelScope.launch { _userAddressState.emit(Resource.Error(it.message)) }
                }
        } else {
            viewModelScope.launch {
                _infoValidateState.emit("All field Are required")
            }
        }
    }


    private fun checkAddressValidation(address: Address): Boolean {
        return address.address.trim().isNotEmpty() && address.fullName.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() && address.street.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() && address.state.trim().isNotEmpty()
    }


}