package com.example.athath.presenntation.categories.base_category_screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.athath.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TableFragment: BaseCategoryFragment(){

    @Inject
    lateinit var firestore: FirebaseFirestore


    private val viewModel by viewModels<BaseCategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Table)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectOfferedProducts()
        collectBestCategoryProducts()

        offeredProductsAdapter.onItemClick = {
            val action = BaseCategoryFragmentDirections
                .actionBaseCategoryFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }

        bestCategoryProductsAdapter.onItemClick = {
            val action = BaseCategoryFragmentDirections
                .actionBaseCategoryFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }
    }


    private fun collectOfferedProducts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.offeredProducts.collect{value ->
                    when(value){
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            offeredProductsAdapter.differ.submitList(value.data)
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Snackbar.make(
                                requireView(),
                                value.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun collectBestCategoryProducts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.bestCategoryProducts.collect{value ->
                    when(value){
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            bestCategoryProductsAdapter.differ.submitList(value.data)
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Snackbar.make(
                                requireView(),
                                value.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}