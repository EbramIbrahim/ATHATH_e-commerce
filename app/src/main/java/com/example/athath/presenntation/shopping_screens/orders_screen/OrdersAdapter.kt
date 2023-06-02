package com.example.athath.presenntation.shopping_screens.orders_screen

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.athath.R
import com.example.athath.data.models.orders.Order
import com.example.athath.data.models.orders.OrderStatus
import com.example.athath.data.models.orders.getOrderStatus
import com.example.athath.databinding.OrderItemBinding

class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orders: Order) {
            binding.apply {
                tvOrderDate.text = orders.date
                tvOrderId.text = orders.ordersId.toString()
                val resource = itemView.resources

                val orderStatusColor = when (getOrderStatus(orders.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resource.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatus.Canceled -> {
                        ColorDrawable(resource.getColor(R.color.g_red))
                    }
                    is OrderStatus.Confirmed -> {
                        ColorDrawable(resource.getColor(R.color.g_green))
                    }
                    is OrderStatus.Shipped -> {
                        ColorDrawable(resource.getColor(R.color.g_green))
                    }
                    is OrderStatus.Delivered -> {
                        ColorDrawable(resource.getColor(R.color.g_green))
                    }
                    is OrderStatus.Returned -> {
                        ColorDrawable(resource.getColor(R.color.g_red))
                    }
                }

                imageOrderState.setImageDrawable(orderStatusColor)


            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.ordersId == newItem.ordersId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val orders = differ.currentList[position]
        holder.bind(orders)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(orders)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Order)-> Unit)? = null
}














