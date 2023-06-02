package com.example.athath.presenntation.categories.base_category_screens


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.databinding.BaseCategoryBinding
import com.example.athath.presenntation.categories.main_category_screen.BestProductsAdapter
import com.example.athath.presenntation.shopping_screens.home_screen.HomeFragmentDirections


// by lazy{} mean we only initialized variable when we call it first time
open class BaseCategoryFragment : Fragment() {
    private var _binding: BaseCategoryBinding? = null
    private val binding get() = _binding!!
    protected val offeredProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestCategoryProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BaseCategoryBinding.inflate(layoutInflater)
        setUpOfferedProducts()
        setUpBestCategoryProducts()




        return binding.root
    }

    fun showLoading(){
        binding.mainPb.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.mainPb.visibility = View.INVISIBLE
    }

    private fun setUpOfferedProducts(){
        binding.rvOfferedProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offeredProductsAdapter
        }
    }

    private fun setUpBestCategoryProducts(){
        binding.rvBestCategoryProducts.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = bestCategoryProductsAdapter
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }




}