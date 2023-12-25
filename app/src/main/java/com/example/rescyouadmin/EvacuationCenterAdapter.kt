package com.example.rescyouadmin

import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ItemEvacuationCentersBinding
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "EvacuationCenterAdapter"

class EvacuationCenterAdapter(private var evacuationCenterArrayList: List<EvacuationCenterData>) :
    RecyclerView.Adapter<EvacuationCenterAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemEvacuationCentersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = evacuationCenterArrayList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = evacuationCenterArrayList[position]
        holder.bind(currentItem)

        // Edit Evacuation Center
        // Open EvacuationCenterEdit.kt and pass the evacuation center data to it
        holder.binding.editEvacuationCenterButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EvacuationCenterEdit::class.java)
            intent.putExtra("evacuationCenterId", currentItem.evacuationCenterId)
            intent.putExtra("placeId", currentItem.placeId)
            intent.putExtra("name", currentItem.name)
            intent.putExtra("status", currentItem.status)
            intent.putExtra("inCharge", currentItem.inCharge)
            intent.putExtra("inChargeContactNum", currentItem.inChargeContactNum)
            intent.putExtra("occupants", currentItem.occupants)
            intent.putExtra("address", currentItem.address)
            intent.putExtra("latitude", currentItem.latitude)
            intent.putExtra("longitude", currentItem.longitude)
            startActivity(context, intent, null)

            Log.d(
                TAG,
                "Place Id: ${currentItem.placeId}, Name: ${currentItem.name}, Status: ${currentItem.status}, In Charge: ${currentItem.inCharge}, In Charge Contact Number: ${currentItem.inChargeContactNum}, Occupants: ${currentItem.occupants}, Address: ${currentItem.address}, Latitude: ${currentItem.latitude}, Longitude: ${currentItem.longitude}"
            )
        }

        //Delete Evacuation Center
        // Delete Evacuation Center
        holder.binding.deleteEvacuationCenterButton.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Delete Evacuation Center")
            builder.setMessage("Are you sure you want to delete this evacuation center?")
            builder.setPositiveButton("Yes") { _, _ ->
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("Evacuation Centers")
                currentItem.evacuationCenterId?.let { it1 -> databaseReference.child(it1).removeValue() }

                // Find the position of the item in the list
                val itemPosition = evacuationCenterArrayList.indexOf(currentItem)

                if (itemPosition != -1) {
                    // Remove the item from the local list
                    evacuationCenterArrayList = evacuationCenterArrayList.toMutableList().apply {
                        removeAt(itemPosition)
                    }

                    // Notify the adapter that the item at the specified position has been removed
                    notifyItemRemoved(itemPosition)
                }

                Log.i(TAG, "Deleted ${currentItem.name}")
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }

    }

    class MyViewHolder(val binding: ItemEvacuationCentersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EvacuationCenterData) {
            // Bind data to the views using view binding
            binding.nameTextview.text = item.name.toString()
            binding.addressTextview.text = item.address.toString()
            binding.statusTextview.text = item.status.toString()
            binding.inChargeTextview.text = item.inCharge.toString()
            binding.inChargeContactNumTextview.text = item.inChargeContactNum.toString()
            binding.occupantsTextview.text = item.occupants.toString()

            // Set background color and text color based on the value of the status
            val context = binding.root.context
            val isAvailable = item.status.equals("AVAILABLE", ignoreCase = true)
            val isFull = item.status.equals("FULL", ignoreCase = true)

            val statusColor = when {
                isAvailable -> R.color.navy_blue
                isFull -> R.color.dark_gray
                else -> R.color.slight_dark_gray
            }

            val tintList = ColorStateList.valueOf(ContextCompat.getColor(context, statusColor))

            // Set compound drawable tint list using TextViewCompat
            TextViewCompat.setCompoundDrawableTintList(binding.statusTextview, tintList)

        }
    }

}

