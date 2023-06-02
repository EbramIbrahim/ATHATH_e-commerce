package com.example.athath.presenntation.shopping_screens.orders_screen.order_details_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athath.data.models.orders.OrderStatus
import com.example.athath.data.models.orders.getOrderStatus
import com.example.athath.databinding.FragmentOrderDetailBinding
import com.example.athath.presenntation.shopping_screens.billing_screen.BillingProductsAdapter
import com.example.athath.utils.VerticalItemDecoration
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrderDetailsScreen: Fragment() {
    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private val billingProductAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailsScreenArgs>()
    private val viewModel by viewModels<OrderDetailsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater)
        setUpRecyclerView()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order


        viewModel.showOrderStatusNotification(order.orderStatus, order.ordersId)

        binding.apply {
            tvOrderId.text = "Order #${order.ordersId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            val currentStep = when(getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentStep, false)
            if (currentStep == 3) stepView.done(true)

            tvFullName.text = order.address.fullName
            tvAddress.text = order.address.city
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "$${order.totalPrice}"

            imageCloseOrder.setOnClickListener {
                findNavController().navigateUp()
            }

            billingProductAdapter.differ.submitList(order.products)


        }
    }




    private fun setUpRecyclerView() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = billingProductAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}




