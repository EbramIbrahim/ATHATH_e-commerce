package com.example.athath.presenntation.shopping_screens.product_details_screen


import android.annotation.SuppressLint
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.data.models.CartProduct
import com.example.athath.databinding.ProductDetailsFragmentBinding
import com.example.athath.utils.Resource
import com.example.athath.utils.hideBottomNavigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private var _binding: ProductDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewPagerAdapter: ProductDetailsViewPagerAdapter by lazy { ProductDetailsViewPagerAdapter() }
    private val colorsAdapter: ProductDetailsColorAdapter by lazy { ProductDetailsColorAdapter() }
    private val sizesAdapter: ProductDetailsSizeAdapter by lazy { ProductDetailsSizeAdapter() }
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private val viewModel by viewModels<AddToCartViewModel>()
    private var selectedColor: Int? = null
    private var selectedSize: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductDetailsFragmentBinding.inflate(layoutInflater)
        val product = args.product

        hideBottomNavigation()

        setUpColorsAdapter()
        setUpSizesAdapter()
        setUpViewPagerAdapter()

        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        binding.closeIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.addProductDetailsBtn.setOnClickListener {
            viewModel.addUpdateCartProduct(
                CartProduct(product, 1, selectedColor, selectedSize)
            )
        }
        collectCartState()

        binding.apply {
            tvProductName.text = product.name
            product.offerPercentage?.let {
                val offer = it * product.price
                val newPrice = product.price - offer
                tvProductPrice.text = "$ ${String.format("%.2f", newPrice)}"
            }
            if (product.offerPercentage == null) tvProductPrice.text = product.price.toString()

            product.description?.let {
                tvProductDescription.text = it
            }
            viewPagerAdapter.differ.submitList(product.images)
            product.sizes?.let {
                sizesAdapter.differ.submitList(it)
            }
            product.colors?.let {
                colorsAdapter.differ.submitList(it)
            }
        }

        return binding.root
    }

    private fun collectCartState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addCartState.collectLatest { result ->
                    when(result) {
                        is Resource.Loading -> {
                            binding.addProductDetailsBtn.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.addProductDetailsBtn.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Product added Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is Resource.Error -> {
                            binding.addProductDetailsBtn.revertAnimation()
                            Snackbar.make(
                                requireView(),
                                result.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }


    private fun setUpViewPagerAdapter(){
        binding.productDetailsVP2.apply {
            adapter = viewPagerAdapter
        }
    }

    private fun setUpColorsAdapter(){
        binding.rvProductColors.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = colorsAdapter
        }
    }

    private fun setUpSizesAdapter(){
        binding.rvProductSize.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sizesAdapter
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}