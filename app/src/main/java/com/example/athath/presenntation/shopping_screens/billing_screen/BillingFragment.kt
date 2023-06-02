package com.example.athath.presenntation.shopping_screens.billing_screen

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.athath.R
import com.example.athath.data.models.Address
import com.example.athath.data.models.CartProduct
import com.example.athath.data.models.orders.Order
import com.example.athath.data.models.orders.OrderStatus
import com.example.athath.databinding.FragmentBillingBinding
import com.example.athath.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private var _binding: FragmentBillingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BillingViewModel>()
    private val billingAddressAdapter by lazy { BillingAddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<BillingFragmentArgs>()
    private var totalPrice = 0f
    private var products = emptyList<CartProduct>()
    private var userAddress: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalPrice = args.totalPrice
        products = args.cartProduct.toList()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingBinding.inflate(inflater)
        setUpBillingRecyclerView()
        setUpBillingProductsRecyclerView()
        collectUserAddresses()
        collectOrderStatus()

        billingAddressAdapter.onItemClick = {
            userAddress = it
        }



        binding.apply {
            tvTotalPrice.text = "$$totalPrice"

            closeBillingIv.setOnClickListener {
                findNavController().navigateUp()
            }

            ivAddAddress.setOnClickListener {
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
            }

            btnPlaceOrder.setOnClickListener {
                userOrdersStatus()
            }
        }

        billingProductAdapter.differ.submitList(products)

        return binding.root
    }


    private fun collectUserAddresses() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.address.collect { address ->
                    when (address) {
                        is Resource.Loading -> {
                            binding.addressPb.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.addressPb.visibility = View.INVISIBLE
                            billingAddressAdapter.differ.submitList(address.data)

                        }

                        is Resource.Error -> {
                            binding.addressPb.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                address.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun userOrdersStatus() {
        if (userAddress == null) {
            Toast.makeText(
                requireContext(),
                "You must select address",
                Toast.LENGTH_LONG
            ).show()
            return
        } else if (products.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Your products cart is Empty",
                Toast.LENGTH_LONG
            ).show()
            return
        } else {
            showOrderConfirmationDialog()
        }
    }

    private fun collectOrderStatus() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.order.collect { order ->
                    when (order) {
                        is Resource.Loading -> {
                            binding.btnPlaceOrder.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.btnPlaceOrder.revertAnimation()
                            Snackbar.make(
                                requireView(),
                                "Your orders was placed Successfully",
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }

                        is Resource.Error -> {
                            binding.btnPlaceOrder.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                order.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    userAddress!!
                )
                viewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }


    private fun setUpBillingRecyclerView() {
        binding.rvAddresses.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingAddressAdapter
        }
    }

    private fun setUpBillingProductsRecyclerView() {
        binding.rvProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductAdapter
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}