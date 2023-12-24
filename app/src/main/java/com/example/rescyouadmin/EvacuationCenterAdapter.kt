package com.example.rescyouadmin

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.TextViewCompat
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

                val statusColor = when {
                    isAvailable -> R.color.navy_blue
                    isFull -> R.color.dark_gray
                    else -> R.color.slight_dark_gray
                }

                val tintList = ColorStateList.valueOf(ContextCompat.getColor(context, statusColor))

                // Set compound drawable tint list using TextViewCompat
                TextViewCompat.setCompoundDrawableTintList(statusTextview, tintList)

                // EDIT EVACUATION CENTER
                binding.editEvacuationCenterButton.setOnClickListener {
                    val intent = Intent(context, EvacuationCenterAddEdit::class.java)
                    intent.putExtra("nameEC", item.name)
                    intent.putExtra("address", item.address)
                    intent.putExtra("status", item.status)
                    intent.putExtra("inCharge", item.inCharge)
                    intent.putExtra("inChargeContactNum", item.inChargeContactNum)
                    intent.putExtra("occupants", item.occupants)
                    startActivity(context, intent, null)
                }


                // DELETE EVACUATION CENTER
                // Open a confirmation dialog before deleting the evacuation center



            }


        }




    }
}
