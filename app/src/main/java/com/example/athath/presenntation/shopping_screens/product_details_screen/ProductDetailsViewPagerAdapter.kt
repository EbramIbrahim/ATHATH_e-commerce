package com.example.athath.presenntation.shopping_screens.product_details_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.databinding.ProductDetailsImagesBinding

class ProductDetailsViewPagerAdapter:
    RecyclerView.Adapter<ProductDetailsViewPagerAdapter.ProductDetailsViewHolder>() {


        inner class ProductDetailsViewHolder(private val binding: ProductDetailsImagesBinding)
            :RecyclerView.ViewHolder(binding.root){

                fun bind(imageUrl: String) {
                    Glide.with(itemView)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.productDetailsImage)
                }
            }



    private val diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailsViewHolder {
        return ProductDetailsViewHolder(
            ProductDetailsImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductDetailsViewHolder, position: Int) {
        val imageUrl = differ.currentList[position]
        holder.bind(imageUrl)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}