package com.example.athath.presenntation.shopping_screens.billing_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.Address
import com.example.athath.data.models.orders.Order
import com.example.athath.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {



    private val _addresses: MutableStateFlow<Resource<List<Address>>> =
        MutableStateFlow(Resource.UnSpecified())

    val address = _addresses.asStateFlow()


    private val _order: MutableStateFlow<Resource<Order>> =
        MutableStateFlow(Resource.UnSpecified())

    val order = _order.asStateFlow()




    init {
        getUserAddresses()
    }

    private fun getUserAddresses() {
        viewModelScope.launch {
            _addresses.emit(Resource.Loading())
            delay(2000L)
        }

        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null){
                    viewModelScope.launch { _addresses.emit(Resource.Error(error.message)) }
                    return@addSnapshotListener
                }
                val result = value?.toObjects(Address::class.java)
                viewModelScope.launch { _addresses.emit(Resource.Success(result)) }
            }

    }

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
            delay(4000L)
        }
        // runBatch let me just writing in database
        firestore.runBatch {
            // TODO: add sub-collection (orders) to user collection
            // TODO: add collection (orders)
            // TODO: delete sub-collection (cart) from user collection

            // 1
            firestore.collection("user").document(auth.uid!!)
                .collection("orders").document().set(order)

            // 2
            firestore.collection("orders").document().set(order)

            // 3
            firestore.collection("user").document(auth.uid!!)
                .collection("cart").get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch { _order.emit(Resource.Success(order)) }
        }.addOnFailureListener {
            viewModelScope.launch { _order.emit(Resource.Error(it.message)) }
        }

    }






}