package com.example.athath.presenntation.shopping_screens.orders_screen

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.databinding.FragmentOrdersBinding
import com.example.athath.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment: Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<OrdersViewModel>()
    private val ordersAdapter by lazy { OrdersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater)
        setUpOrdersAdapter()
        collectOrdersStatus()

        ordersAdapter.onItemClick = {
            val action = OrdersFragmentDirections
                .actionOrdersFragmentToOrderDetailsScreen(it)
            findNavController().navigate(action)
        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }


    private fun collectOrdersStatus() {
        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ordersStatus.collect {orders ->
                    when(orders) {
                        is Resource.Loading -> {
                            binding.progressbarAllOrders.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarAllOrders.visibility = View.INVISIBLE
                            ordersAdapter.differ.submitList(orders.data)
                        }
                        is Resource.Error -> {
                            binding.progressbarAllOrders.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                orders.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }


    private fun setUpOrdersAdapter() {
        binding.rvAllOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}