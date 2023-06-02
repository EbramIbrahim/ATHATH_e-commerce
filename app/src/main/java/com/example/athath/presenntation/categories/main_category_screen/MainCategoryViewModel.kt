package com.example.athath.presenntation.categories.main_category_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.Product
import com.example.athath.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {


    private val _specialCategory: MutableStateFlow<Resource<List<Product>>> =
        MutableStateFlow(Resource.UnSpecified())
    val specialCategory = _specialCategory.asStateFlow()

    private val _bestDealsCategory: MutableStateFlow<Resource<List<Product>>> =
        MutableStateFlow(Resource.UnSpecified())
    val bestDealsCategory = _bestDealsCategory.asStateFlow()

    private val _bestProductsCategory: MutableStateFlow<Resource<List<Product>>> =
        MutableStateFlow(Resource.UnSpecified())
    val bestProductsCategory = _bestProductsCategory.asStateFlow()


    init {
        getSpecialProducts()
        getBestDealsProducts()
        getBestProducts()
    }


    private fun getSpecialProducts() {

        fireStore.collection("Products")
            .whereEqualTo("category", "Special Products")
            .get().addOnSuccessListener { result ->
                val product = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialCategory.emit(Resource.Loading())
                    delay(2000L)

                    _specialCategory.emit(Resource.Success(product))
                }
            }.addOnFailureListener {
                viewModelScope.launch { _specialCategory.emit(Resource.Error(it.toString())) }
            }
    }

    private fun getBestDealsProducts() {
        fireStore.collection("Products")
            .whereEqualTo("category", "Best deals")
            .get().addOnSuccessListener { result ->
                val product = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsCategory.emit(Resource.Loading())
                    delay(2000L)

                    _bestDealsCategory.emit(Resource.Success(product))
                }
            }.addOnFailureListener {
                viewModelScope.launch { _bestDealsCategory.emit(Resource.Error(it.toString())) }
            }
    }

    private fun getBestProducts() {
        fireStore.collection("Products")
            .whereEqualTo("category", "Best Products")
            .get().addOnSuccessListener { result ->
                val product = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProductsCategory.emit(Resource.Loading())
                    delay(2000L)

                    _bestProductsCategory.emit(Resource.Success(product))
                }
            }.addOnFailureListener {
                viewModelScope.launch { _bestProductsCategory.emit(Resource.Error(it.toString())) }
            }
    }


}


















