package com.example.athath.presenntation.shopping_screens.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athath.data.models.Product
import com.example.athath.utils.Constant.DELAY_DURATION
import com.example.athath.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {



    private val _searchState: MutableStateFlow<Resource<List<Product>>> =
        MutableStateFlow(Resource.UnSpecified())
    val searchState = _searchState.asStateFlow()





    fun getProductsBySearch(category: String){
        viewModelScope.launch {
            _searchState.emit(Resource.Loading())
            delay(DELAY_DURATION)
        }

        firestore.collection("Products")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener {value ->
                val result = value.toObjects(Product::class.java)
                viewModelScope.launch { _searchState.emit(Resource.Success(result)) }
            }.addOnFailureListener {
                viewModelScope.launch { _searchState.emit(Resource.Error(it.message)) }
            }

    }





}

















