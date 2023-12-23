package com.example.rescyouadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ItemEvacuationCentersBinding

class EvacuationCenterAdapter(private val evacuationCenterArrayList: List<EvacuationCenterData>) :
    RecyclerView.Adapter<EvacuationCenterAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEvacuationCentersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = evacuationCenterArrayList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(evacuationCenterArrayList[position])
    }

    class MyViewHolder(private val binding: ItemEvacuationCentersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EvacuationCenterData) {
            with(binding) {
                // Bind data to the views using view binding
                nameTextview.text = item.name.toString()
                addressTextview.text = item.address.toString()
                statusTextview.text = item.status.toString()
                inChargeTextview.text = item.inCharge.toString()
                inChargeContactNumTextview.text = item.inChargeContactNum.toString()
                occupantsTextview.text = item.occupants.toString()

                // Set background color and text color based on the value of the status
                val context = root.context
                val isAvailable = item.status.equals("AVAILABLE", ignoreCase = true)
                val isFull = item.status.equals("FULL", ignoreCase = true)

                val statusBgColorResId = when {
                    isAvailable -> R.color.navy_blue
                    isFull -> R.color.slight_dark_gray
                    else -> R.color.slight_dark_gray // Adjust to your default background color
                }

                val statusTextColorResId = when {
                    isAvailable -> R.color.light_gray
                    isFull -> R.color.navy_blue
                    else -> R.color.darker_gray // Adjust to your default text color
                }

                statusTextview.setBackgroundColor(ContextCompat.getColor(context, statusBgColorResId))
                statusTextview.setTextColor(ContextCompat.getColor(context, statusTextColorResId))
            }
        }
    }
}
