package com.example.rescyouadmin

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ItemAllPinsBinding

private const val TAG = "ShowAllPinsAdapter"
class ShowAllPinsAdapter(private val pinsArrayList: List<PinDataClass>) :
    RecyclerView.Adapter<ShowAllPinsAdapter.MyViewHolder>() {

    // This function is responsible for creating new views when needed by the layout manager.
    // It returns a new ViewHolder, which will hold the inflated layout of the item view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAllPinsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // This function is responsible for returning the size of the list.
    // It is used in determining how many evacuation centers are in the database.
    override fun getItemCount(): Int = pinsArrayList.size

    // This function is responsible for binding the data to the views.
    // It is called by the layout manager when it wants new data to be displayed.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = pinsArrayList[position]
        holder.bind(currentItem)
    }

    class MyViewHolder(val binding: ItemAllPinsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PinDataClass) {
            // Bind data to the views using view binding
            binding.nameTextview.text = item.pinName.toString()
            binding.rescuedByTextview.text = item.pinRescuer.toString()
            binding.ratingsTextview.text = item.rate.toString()
            binding.disasterTextview.text = item.disasterType.toString()
            binding.sitioTextview.text = item.sitio.toString()
            binding.descriptionTextview.text = item.description.toString()

            binding.dateTextview.text = item.date.toString()
            binding.timeTextview.text = item.time.toString()

            // !! This if for the Show All Pins activity only !!
            // Set background color and text based on the value of the status
            val context = binding.root.context
            val isResolved = item.resolved.equals("true", ignoreCase = true)
            val isActionRequired = item.resolved.equals("false", ignoreCase = true)

            val statusColor: Int
            val statusText: String

            when {
                isActionRequired -> {
                    statusColor = R.color.isFull
                    statusText = "ACTION REQUIRED"
                }
                isResolved -> {
                    statusColor = R.color.isAvailable
                    statusText = "RESOLVED"
                }
                else -> {
                    statusColor = R.color.isNotAvailable
                    statusText = "IN-PROGRESS"
                }
            }

            val tintList = ColorStateList.valueOf(ContextCompat.getColor(context, statusColor))

// Set compound drawable tint list using TextViewCompat
            TextViewCompat.setCompoundDrawableTintList(binding.statusTextview, tintList)
            binding.statusTextview.text = statusText
        }
    }

}