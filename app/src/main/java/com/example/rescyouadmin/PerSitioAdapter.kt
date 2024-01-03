package com.example.rescyouadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PerSitioAdapter(private val perSitioList: List<SitioDataClass>) : RecyclerView.Adapter<PerSitioAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sitioNameTextView: TextView = itemView.findViewById(R.id.sitioName_textview)
        val persitioTotalTextView: TextView = itemView.findViewById(R.id.persitio_total_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_persitio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sitioData = perSitioList[position]
        holder.sitioNameTextView.text = sitioData.sitioName
        holder.persitioTotalTextView.text = sitioData.total.toString() // Assuming 'total' is a property in SitioDataClass


        // Add bottom margin to the last item
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == itemCount - 1) {
            layoutParams.bottomMargin = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.margin_bottom_last_item_smaller)
        } else {
            layoutParams.bottomMargin = 0
        }
    }

    override fun getItemCount() = perSitioList.size
}