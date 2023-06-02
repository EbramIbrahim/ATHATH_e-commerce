package com.example.athath.presenntation.shopping_screens.product_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.CartProduct
import com.example.athath.utils.FirebaseUtils
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
class AddToCartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils
): ViewModel() {


    private val _addCartState: MutableStateFlow<Resource<CartProduct>> =
        MutableStateFlow(Resource.UnSpecified())

    val addCartState = _addCartState.asStateFlow()




    fun addUpdateCartProduct(cartProduct: CartProduct) {
        viewModelScope.launch {
            _addCartState.emit(Resource.Loading())
            delay(2000L)
        }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let { document ->
                    if (document.isEmpty()){ // add new product to cart
                        addProductToCart(cartProduct)
                    } else {
                        val product = document.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) { // same product then, increase quantity
                            val documentId = it.first().id
                            increaseProductQuantity(documentId, cartProduct)
                        } else { // add new product to cart
                            addProductToCart(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addCartState.emit(Resource.Error(it.message.toString())) }
            }
    }

    private fun addProductToCart(cartProduct: CartProduct) {
        firebaseUtils.addToCart(cartProduct) { product, exception ->
            viewModelScope.launch {
                if (exception == null)
                    _addCartState.emit(Resource.Success(product))
                else
                    _addCartState.emit(Resource.Error(exception.message.toString()))
            }

        }
    }

    private fun increaseProductQuantity(documentId: String,cartProduct: CartProduct) {
        firebaseUtils.increaseProductQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null)
                    _addCartState.emit(Resource.Success(cartProduct))
                else
                    _addCartState.emit(Resource.Error(exception.message.toString()))
            }

        }
    }



}













