package com.example.athath.presenntation.shopping_screens.cart_screen

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.data.models.CartProduct
import com.example.athath.data.models.Product
import com.example.athath.databinding.ProductCartItemsBinding
import com.example.athath.databinding.SpecialRvItemBinding
import com.example.athath.presenntation.categories.main_category_screen.SpecialItemAdapter

class CartProductsAdapter : RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder(val binding: ProductCartItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView)
                    .load(cartProduct.product.images[0])
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(productCartIv)


                productCartColorIv.setImageDrawable(
                    ColorDrawable(
                        cartProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                productCartSizeTv.text = cartProduct.selectedSize
                    ?: "".also { productCartSizeIv.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }

                tvProductQuantity.text = cartProduct.quantity.toString()
                productCartNameTv.text = cartProduct.product.name

                cartProduct.product.offerPercentage?.let {
                    val offer = it * cartProduct.product.price
                    val newPrice = cartProduct.product.price - offer
                    productCartPriceTv.text = "$ ${String.format("%.2f", newPrice)}"

                }
                if (cartProduct.product.offerPercentage == null) {
                    productCartPriceTv.text = cartProduct.product.price.toString()
                }
            }
        }
    }


    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            ProductCartItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(cartProduct)
        }

        holder.binding.plusIv.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.minusIv.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null
}