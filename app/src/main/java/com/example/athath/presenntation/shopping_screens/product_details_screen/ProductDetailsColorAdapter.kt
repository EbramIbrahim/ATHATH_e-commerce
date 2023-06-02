package com.example.athath.presenntation.shopping_screens.product_details_screen

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.athath.databinding.ProductDetailsColorsBinding

class ProductDetailsColorAdapter
    :RecyclerView.Adapter<ProductDetailsColorAdapter.ProductDetailsColorViewHolder>(){


    private var selectedPosition = -1

    inner class ProductDetailsColorViewHolder(private val binding: ProductDetailsColorsBinding)
        :RecyclerView.ViewHolder(binding.root){

            fun bind(color: Int, position: Int){
                val imageColor = ColorDrawable(color)
                binding.productImageColor.setImageDrawable(imageColor)
                if (position == selectedPosition) { // color is selected
                    binding.productImageShadow.visibility = View.VISIBLE
                    binding.productImageSelect.visibility = View.VISIBLE
                } else {// color isn't selected
                    binding.productImageShadow.visibility = View.INVISIBLE
                    binding.productImageSelect.visibility = View.INVISIBLE
                }
            }
        }

    private val diffUtil = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailsColorViewHolder {
        return ProductDetailsColorViewHolder(
            ProductDetailsColorsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductDetailsColorViewHolder, position: Int) {
        val productColor = differ.currentList[position]
        holder.bind(productColor, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(productColor)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

     var onItemClick: ((Int) -> Unit)? = null
}



