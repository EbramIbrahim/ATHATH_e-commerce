package com.example.athath.presenntation.shopping_screens.product_details_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.athath.databinding.ProductDetailsSizeBinding

class ProductDetailsSizeAdapter
    : RecyclerView.Adapter<ProductDetailsSizeAdapter.ProductDetailsSizeViewHolder>() {

    private var selectedPosition = -1

    inner class ProductDetailsSizeViewHolder(private val binding: ProductDetailsSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.productImageSize.text = size
            if (position == selectedPosition) { // size is selected
                binding.productSizeShadow.visibility = View.VISIBLE
            } else {// size isn't selected
                binding.productSizeShadow.visibility = View.INVISIBLE
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailsSizeViewHolder {
        return ProductDetailsSizeViewHolder(
            ProductDetailsSizeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductDetailsSizeViewHolder, position: Int) {
        val productSize = differ.currentList[position]
        holder.bind(productSize, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(productSize)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

     var onItemClick: ((String) -> Unit)? = null
}