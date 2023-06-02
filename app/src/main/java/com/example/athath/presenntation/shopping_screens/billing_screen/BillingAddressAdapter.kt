package com.example.athath.presenntation.shopping_screens.billing_screen

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.athath.R
import com.example.athath.data.models.Address
import com.example.athath.databinding.AddressItemsBinding

class BillingAddressAdapter: RecyclerView.Adapter<BillingAddressAdapter.BillingViewHolder>() {

    inner class BillingViewHolder(val binding: AddressItemsBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(currentAddress: Address?, isSelected: Boolean) {
            binding.apply {
                addressBtn.text = currentAddress?.address
                if (isSelected){
                    addressBtn.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                } else {
                    addressBtn.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))

                }
            }
        }
    }




    private val diffCallBack = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.address == newItem.address && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(
            AddressItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private var addressPosition = -1
    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val currentAddress = differ.currentList[position]
        holder.bind(currentAddress, addressPosition == position)

        holder.binding.addressBtn.setOnClickListener {
            if (addressPosition >= 0)
                notifyItemChanged(addressPosition)
            addressPosition = holder.adapterPosition
            notifyItemChanged(addressPosition)
            onItemClick?.invoke(currentAddress)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Address) -> Unit)? = null
}