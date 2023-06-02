package com.example.athath.presenntation.categories.main_category_screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.data.models.Product
import com.example.athath.databinding.SpecialRvItemBinding

class SpecialItemAdapter: RecyclerView.Adapter<SpecialItemAdapter.SpecialItemViewHolder>() {

    inner class SpecialItemViewHolder(private val binding: SpecialRvItemBinding)
        :RecyclerView.ViewHolder(binding.root){

            @SuppressLint("SetTextI18n")
            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView)
                        .load(product.images[0])
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(specialItemIm)

                    specialItemNameTv.text = product.name
                    specialItemPriceTv.text = "$ ${product.price}"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialItemViewHolder {
       return SpecialItemViewHolder(
           SpecialRvItemBinding.inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: SpecialItemViewHolder, position: Int) {
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