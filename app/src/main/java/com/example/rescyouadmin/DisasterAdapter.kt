package com.example.rescyouadmin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DisasterAdapter(private val context: Context, private var dataList: List<DataClass>) : RecyclerView.Adapter<DisasterAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        //TITLE OR DISASTER NAME/CATEGORY
        val recTitle: TextView = itemView.findViewById(R.id.disaster_category)

        //BUTTON
        val editButton : TextView = itemView.findViewById(R.id.editDisasterButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_per_disaster_tips, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //TEXT
        holder.recTitle.text = dataList[position].dataTitle

        //BUTTON
        holder.editButton.setOnClickListener {
            val intent = Intent(context, EdiDisasterCategory::class.java)
            intent.putExtra("Image", dataList[holder.adapterPosition].dataImage)
            intent.putExtra("Title", dataList[holder.adapterPosition].dataTitle)
            intent.putExtra("Description", dataList[holder.adapterPosition].dataDesc)

            intent.putExtra("Image Source", dataList[holder.adapterPosition].dataImageSource)
            intent.putExtra("Article Source", dataList[holder.adapterPosition].dataArticleSource)

            context.startActivity(intent)
        }

        // Add bottom margin to the last item
        // This will avoid the last item from being hidden by the fab
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == itemCount - 1) {
            layoutParams.bottomMargin = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.margin_bottom_last_item_smaller)
        } else {
            layoutParams.bottomMargin = 0
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}