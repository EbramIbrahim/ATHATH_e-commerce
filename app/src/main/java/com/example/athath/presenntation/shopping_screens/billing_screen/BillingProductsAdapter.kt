package com.example.athath.presenntation.shopping_screens.billing_screen

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.athath.R
import com.example.athath.data.models.Address
import com.example.athath.data.models.CartProduct
import com.example.athath.databinding.AddressItemsBinding
import com.example.athath.databinding.BillingInfoItemBinding
import com.example.athath.utils.offer

class BillingProductsAdapter: RecyclerView.Adapter<BillingProductsAdapter.BillingProductViewHolder>() {

    inner class BillingProductViewHolder(val binding: BillingInfoItemBinding)
        :RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView)
                    .load(cartProduct.product.images[0])
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(productBillingIv)


                productBillingColorIv.setImageDrawable(
                    ColorDrawable(
                        cartProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                productBillingSizeTv.text = cartProduct.selectedSize
                    ?: "".also { productBillingSizeIv.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }

                tvProductQuantity.text = cartProduct.quantity.toString()
                productBillingNameTv.text = cartProduct.product.name


               val price = cartProduct.product.offerPercentage.offer(cartProduct.product.price)
                productBillingPriceTv.text = "$$price"

            }
        }

    }




    private val diffCallBack = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewHolder {
        return BillingProductViewHolder(
            BillingInfoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BillingProductViewHolder, position: Int) {
        val currentProduct = differ.currentList[position]

        holder.bind(currentProduct)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}