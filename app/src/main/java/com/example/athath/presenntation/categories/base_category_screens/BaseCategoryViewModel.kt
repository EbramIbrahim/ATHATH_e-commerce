package com.example.athath.presenntation.categories.base_category_screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.Product
import com.example.athath.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaseCategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _offeredProducts: MutableStateFlow<Resource<List<Product>>> =
        MutableStateFlow(Resource.UnSpecified())
    val offeredProducts = _offeredProducts.asStateFlow()

    private val _bestCategoryProducts: MutableStateFlow<Resource<List<Product>>> =
        MutableStateFlow(Resource.UnSpecified())
    val bestCategoryProducts = _bestCategoryProducts.asStateFlow()


    init {
        getOfferedProducts()
        getBestCategoryProducts()
    }

    private fun getOfferedProducts() {
        firestore.collection("Products")
            .whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).get()
            .addOnSuccessListener { value ->
                val result = value.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offeredProducts.emit(Resource.Loading())
                    delay(2000L)
                    _offeredProducts.emit(Resource.Success(result))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offeredProducts.emit(Resource.Error(it.message))
                }
            }

    }

    private fun getBestCategoryProducts() {
        firestore.collection("Products")
            .whereEqualTo("category", category.category)
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener { value ->
                val result = value.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestCategoryProducts.emit(Resource.Loading())
                    delay(2000L)
                    _bestCategoryProducts.emit(Resource.Success(result))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestCategoryProducts.emit(Resource.Error(it.message))
                }
            }

    }


}