package com.example.athath.presenntation.shopping_screens.cart_screen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.R
import com.example.athath.data.models.CartProduct
import com.example.athath.databinding.CartFragmentBinding
import com.example.athath.utils.FirebaseUtils
import com.example.athath.utils.Resource
import com.example.athath.utils.VerticalItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment: Fragment() {
    private var _binding: CartFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<CartViewModel>()
    private val cartAdapter by lazy { CartProductsAdapter() }
    private var cartTotalPrice: Float = 0f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CartFragmentBinding.inflate(layoutInflater)
        setUpRecyclerView()
        collectCartProducts()
        collectTotalPrice()

        cartAdapter.onItemClick = {
            val action = CartFragmentDirections
                .actionCartFragmentToProductDetailsFragment(it.product)
            findNavController().navigate(action)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseUtils.IncreaseDecreaseProduct.INCREASE)
        }
        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseUtils.IncreaseDecreaseProduct.DECREASE)

        }

        collectProductDeleteState()


        binding.closeCartIv.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.checkOutBtn.setOnClickListener {
            if (viewModel.cartProducts.value.data?.isEmpty() == true){
                Toast.makeText(
                    requireContext(),
                    "Cart is Empty",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val action = cartTotalPrice.let { price ->
                    CartFragmentDirections
                        .actionCartFragmentToBillingFragment(price, cartAdapter.differ.currentList.toTypedArray())
                }
                findNavController().navigate(action)
            }

        }

        return binding.root
    }




    private fun collectCartProducts() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest {result ->
                    when(result) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvTotalPrice.text = ""
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            if (result.data?.isEmpty() == true){
                                whenCartProductsIsEmpty()
                            } else {
                                whenCartProductsIsNotEmpty()
                                cartAdapter.differ.submitList(result.data)
                            }
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                result.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun collectProductDeleteState(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteCartProduct.collectLatest { cartProduct ->
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete item from cart")
                        setMessage("Do you want to delete this item from cart?")
                        setNegativeButton("cancel") {dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Yes"){dialog, _ ->
                            viewModel.deleteCartProduct(cartProduct)
                            dialog.dismiss()
                        }
                    }
                    alertDialog.create()
                    alertDialog.show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun collectTotalPrice() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.productsPrice.collectLatest {
                    it?.let {
                        binding.tvTotalPrice.text = "$$it"
                        cartTotalPrice  = it
                    }
                }
            }
        }
    }

    private fun whenCartProductsIsEmpty() {
        binding.apply {
            emptyCartLayout.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.INVISIBLE
            rvProductCart.visibility = View.INVISIBLE
        }
    }

    private fun whenCartProductsIsNotEmpty() {
        binding.apply {
            emptyCartLayout.visibility = View.GONE
            totalBoxContainer.visibility = View.VISIBLE
            rvProductCart.visibility = View.VISIBLE

        }
    }



    private fun setUpRecyclerView() {
        binding.rvProductCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}











