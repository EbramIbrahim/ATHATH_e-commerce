package com.example.athath.presenntation.categories.main_category_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.R
import com.example.athath.databinding.MainCategoryBinding
import com.example.athath.presenntation.shopping_screens.home_screen.HomeFragmentDirections
import com.example.athath.utils.Resource
import com.example.athath.utils.showBottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment: Fragment() {
    private var _binding: MainCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainCategoryViewModel>()
    private val specialItemAdapter by lazy { SpecialItemAdapter() }
    private val bestProductAdapter by lazy { BestProductsAdapter() }
    private val bestDealsAdapter by lazy { BestDealsAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainCategoryBinding.inflate(layoutInflater, container, false)

        binding.bestDealsTv.visibility = View.INVISIBLE
        binding.bestProductTv.visibility = View.INVISIBLE

        collectSpecialProduct()
        collectBestDealsProduct()
        collectBestProduct()
        setUpSpecialRecyclerView()
        setUpBestDealsRecyclerView()
        setUpBestProductsRecyclerView()

        specialItemAdapter.onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }

        bestDealsAdapter.onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }

        bestProductAdapter.onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }



        return binding.root
    }


    private fun collectSpecialProduct() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.specialCategory.collect { product ->
                    when(product) {
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            specialItemAdapter.differ.submitList(product.data)
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Toast.makeText(
                                requireContext(), product.message, Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> hideLoading()
                    }
                }
            }
        }
    }

    private fun collectBestDealsProduct() {
        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.bestDealsCategory.collect { product ->
                    when(product){
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            bestDealsAdapter.differ.submitList(product.data)
                            binding.bestDealsTv.visibility = View.VISIBLE
                            binding.bestProductTv.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Toast.makeText(
                                requireContext(), product.message, Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun collectBestProduct() {
        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.bestProductsCategory.collect { product ->
                    when(product){
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            hideLoading()
                            bestProductAdapter.differ.submitList(product.data)
                            binding.bestDealsTv.visibility = View.VISIBLE
                            binding.bestProductTv.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Toast.makeText(
                                requireContext(), product.message, Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }



    private fun setUpSpecialRecyclerView() {
        binding.rvSpecialProduct.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            adapter = specialItemAdapter
        }
    }
    private fun setUpBestDealsRecyclerView() {
        binding.rvBestDeals.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            adapter = bestDealsAdapter
        }
    }

    private fun setUpBestProductsRecyclerView() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(
                requireContext(),2,GridLayoutManager.VERTICAL, false
            )
            adapter = bestProductAdapter
        }
    }

    private fun showLoading(){
        binding.mainPb.visibility = View.VISIBLE

    }

    private fun hideLoading(){
        binding.mainPb.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}