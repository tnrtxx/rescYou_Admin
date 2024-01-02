package com.example.rescyouadmin

import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ItemEvacuationCentersBinding
import com.google.firebase.database.FirebaseDatabase

/**
 * EvacuationCenterAdapter
 * Adapter for the RecyclerView in the EvacuationCenters activity
 *
 * evacuationCenterArrayList: It contains the list of evacuation centers to be displayed.
 */

private const val TAG = "EvacuationCenterAdapter"

class EvacuationCenterAdapter(private var evacuationCenterArrayList: List<EvacuationCenterData>) :
    RecyclerView.Adapter<EvacuationCenterAdapter.MyViewHolder>() {

    // This function is responsible for creating new views when needed by the layout manager.
    // It returns a new ViewHolder, which will hold the inflated layout of the item view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEvacuationCentersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // This function is responsible for returning the size of the list.
    // It is used in determining how many evacuation centers are in the database.
    override fun getItemCount(): Int = evacuationCenterArrayList.size

    // This function is responsible for binding the data to the views.
    // It is called by the layout manager when it wants new data to be displayed.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = evacuationCenterArrayList[position]
        holder.bind(currentItem)

        // Edit (Update) Evacuation Center Button
        holder.binding.editEvacuationCenterButton.setOnClickListener {
            // Launch the EvacuationCenterEdit activity and pass the evacuation center data to it
            val context = holder.itemView.context
            val intent = Intent(context, EvacuationCenterEdit::class.java)
            intent.putExtra("evacuationCenterId", currentItem.evacuationCenterId)
            intent.putExtra("placeId", currentItem.placeId)
            intent.putExtra("name", currentItem.name)
            intent.putExtra("status", currentItem.status)
            intent.putExtra("occupants", currentItem.occupants)
            intent.putExtra("address", currentItem.address)
            intent.putExtra("latitude", currentItem.latitude)
            intent.putExtra("longitude", currentItem.longitude)
            startActivity(context, intent, null)

            // Logs the data of the evacuation center that was clicked
            // !! This is for debugging purposes only!!
            // TODO: Remove this later
            Log.d(TAG, toString())
        }

        // Delete Evacuation Center Button
        holder.binding.deleteEvacuationCenterButton.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Delete Evacuation Center")
            builder.setMessage("This action cannot be undone. Are you sure you want to delete this evacuation center? ")
            builder.setPositiveButton("Yes") { _, _ ->
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("Evacuation Centers")
                currentItem.evacuationCenterId?.let { it1 ->
                    databaseReference.child(it1).removeValue()
                }

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

                // Show a toast message to the user that the evacuation center has been deleted
                Toast.makeText(context, "Successfully deleted ${currentItem.name} from the list of evacuation centers.", Toast.LENGTH_SHORT).show()

            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }

        // Add bottom margin to the last item
        // This will avoid the last item from being hidden by the fab
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == itemCount - 1) {
            layoutParams.bottomMargin = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.margin_bottom_last_item)
        } else {
            layoutParams.bottomMargin = 0
        }
    }

    // This class is responsible for holding the views that will be used to display the data.
    class MyViewHolder(val binding: ItemEvacuationCentersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EvacuationCenterData) {
            // Bind data to the views using view binding
            binding.nameTextview.text = item.name.toString()
            binding.addressTextview.text = item.address.toString()
            binding.statusTextview.text = item.status.toString()
            binding.occupantsTextview.text = item.occupants.toString()

            // !! This if for the Evacuation Centers activity only !!
            // Set background color and text color based on the value of the status
            val context = binding.root.context
            val isAvailable = item.status.equals("AVAILABLE", ignoreCase = true)
            val isFull = item.status.equals("FULL", ignoreCase = true)

            val statusColor = when {
                isAvailable -> R.color.isAvailable
                isFull -> R.color.isFull
                else -> R.color.isNotAvailable
            }

            val tintList = ColorStateList.valueOf(ContextCompat.getColor(context, statusColor))

            // Set compound drawable tint list using TextViewCompat
            TextViewCompat.setCompoundDrawableTintList(binding.statusTextview, tintList)

        }
    }

}

