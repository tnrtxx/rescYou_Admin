package com.example.rescyouadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PerDisasterTotalAdapter(private val perDisasterList: List<PerDisasterTotalDataClass>) : RecyclerView.Adapter<PerDisasterTotalAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val disasterNameTextView: TextView = itemView.findViewById(R.id.disasterName_textview)
        val perdisasterTotalTextView: TextView = itemView.findViewById(R.id.perdisaster_total_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_per_disaster_total, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val disasterData = perDisasterList[position]
        holder.disasterNameTextView.text = disasterData.disasterName // Assuming 'disasterName' is a property in PerDisasterTotalDataClass
        holder.perdisasterTotalTextView.text = disasterData.total.toString()
    }

    override fun getItemCount() = perDisasterList.size
}