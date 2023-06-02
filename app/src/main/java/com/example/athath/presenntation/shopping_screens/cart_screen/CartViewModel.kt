package com.example.athath.presenntation.shopping_screens.cart_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.CartProduct
import com.example.athath.utils.FirebaseUtils
import com.example.athath.utils.Resource
import com.example.athath.utils.offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils
) : ViewModel() {


    private val _cartProducts: MutableStateFlow<Resource<List<CartProduct>>> =
        MutableStateFlow(Resource.UnSpecified())

    val cartProducts = _cartProducts.asStateFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    private val _deleteCartProduct: MutableSharedFlow<CartProduct> = MutableSharedFlow()
    val deleteCartProduct = _deleteCartProduct.asSharedFlow()


    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                totalCartProductsPrice(it.data!!)
            }

            else -> null
        }
    }

    private fun totalCartProductsPrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.offer(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }


    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
            delay(2000L)
        }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            //using addSnapshotListener because it executed when ever changes happen in cart collection
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message)) }
                } else {
                    cartProductDocuments = value.documents
                    val cartProduct = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProduct)) }
                }
            }
    }


    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanges: FirebaseUtils.IncreaseDecreaseProduct
    ) {
        // getting the index of product to change quantity of specific product
        val index = cartProducts.value.data?.indexOf(cartProduct)

        // index might equal -1

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanges) {
                FirebaseUtils.IncreaseDecreaseProduct.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    firebaseUtils.increaseProductQuantity(documentId) { _, e ->
                        if (e != null)
                            viewModelScope.launch { _cartProducts.emit(Resource.Error(e.message)) }
                    }
                }

                FirebaseUtils.IncreaseDecreaseProduct.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        // we put product on _deleteCartProduct when the quantity equal 1
                        // to delete it if it equal to 0 not more than it
                        // and accessed only if MinusBtn pressed
                        viewModelScope.launch { _deleteCartProduct.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    firebaseUtils.decreaseProductQuantity(documentId) { _, e ->

                        if (e != null)
                            viewModelScope.launch { _cartProducts.emit(Resource.Error(e.message)) }
                    }
                }
            }
        }
    }

    fun deleteCartProduct(cartProduct: CartProduct) {
            val index = cartProducts.value.data?.indexOf(cartProduct)
            if (index != null && index != -1) {
                val documentId = cartProductDocuments[index].id
                firestore.collection("user").document(auth.uid!!)
                    .collection("cart").document(documentId).delete()

            }

    }


}