package com.example.athath.presenntation.shopping_screens.orders_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.orders.Order
import com.example.athath.utils.Constant.DELAY_DURATION
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
class OrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {


    private val _ordersStatus: MutableStateFlow<Resource<List<Order>>> =
        MutableStateFlow(Resource.UnSpecified())
    val ordersStatus = _ordersStatus.asStateFlow()



    init {
        getUserOrders()
    }


    private fun getUserOrders() {
        viewModelScope.launch {
            _ordersStatus.emit(Resource.Loading())
            delay(DELAY_DURATION)
        }
        firestore.collection("user").document(auth.uid!!)
            .collection("orders").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch { _ordersStatus.emit(Resource.Success(orders)) }
            }.addOnFailureListener {
                viewModelScope.launch { _ordersStatus.emit(Resource.Error(it.message)) }
            }
    }




}




