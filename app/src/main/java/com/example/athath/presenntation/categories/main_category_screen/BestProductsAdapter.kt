package com.example.athath.presenntation.categories.main_category_screen

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.data.models.Product
import com.example.athath.databinding.ProductRvItemBinding
import com.example.athath.databinding.SpecialRvItemBinding

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductViewHolder>() {

    inner class BestProductViewHolder(private val binding: ProductRvItemBinding)
        :RecyclerView.ViewHolder(binding.root){

            @SuppressLint("SetTextI18n")
            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView)
                        .load(product.images[0])
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imgProduct)

                    product.offerPercentage?.let {
                        val offer = it * product.price
                        val newPrice = product.price - offer
                        tvNewPrice.text = "$ ${String.format("%.2f", newPrice)}"
                        tvOldPrice.text = product.price.toString()
                        tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    if (product.offerPercentage == null){
                        tvOldPrice.visibility = View.INVISIBLE
                        tvNewPrice.text = "$ ${product.price}"
                    }

                    tvName.text = product.name
                }
            }
        }


    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
       return BestProductViewHolder(
           ProductRvItemBinding.inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val currentProduct = differ.currentList[position]
        holder.bind(currentProduct)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onItemClick: ((Product) -> Unit)? = null

}