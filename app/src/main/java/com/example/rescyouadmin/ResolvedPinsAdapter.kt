package com.example.rescyouadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rescyouadmin.databinding.ItemResolvedpinsBinding

class ResolvedPinsAdapter(private val pins: MutableList<PinDataClass>) : RecyclerView.Adapter<ResolvedPinsAdapter.PinViewHolder>() {

    class PinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pinNameTextView: TextView = itemView.findViewById(R.id.pinnedby_name_textview)
        val rescuedByTextView: TextView = itemView.findViewById(R.id.rescued_by_textview)
        val ratingsTextView: TextView = itemView.findViewById(R.id.ratings_textview)
        val disasterTextView: TextView = itemView.findViewById(R.id.disaster_textview)
        val sitioTextView: TextView = itemView.findViewById(R.id.sitio_textview)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_textview)
        val attachmentsRecyclerView: RecyclerView = itemView.findViewById(R.id.attachments_recycler_view)

        //DATE AND TIME
        val dateTextView: TextView = itemView.findViewById(R.id.date_textview)
        val timeTextView: TextView = itemView.findViewById(R.id.time_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resolvedpins, parent, false)
        return PinViewHolder(view)
    }

    override fun onBindViewHolder(holder: PinViewHolder, position: Int) {
        val pin = pins[position]
        holder.pinNameTextView.text = pin.pinName
        holder.rescuedByTextView.text = pin.pinRescuer
        holder.ratingsTextView.text = pin.rate
        holder.disasterTextView.text = pin.disasterType
        holder.sitioTextView.text = pin.sitio
        holder.descriptionTextView.text = pin.description
        holder.attachmentsRecyclerView.adapter = AttachmentsAdapter(pin.attachmentList)

        //DATE AND TIME
        holder.dateTextView.text = pin.date
        holder.timeTextView.text = pin.time


        // Add bottom margin to the last item
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == itemCount - 1) {
            layoutParams.bottomMargin = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.margin_bottom_last_item_smaller)
        } else {
            layoutParams.bottomMargin = 0
        }
    }

    override fun getItemCount() = pins.size

    class AttachmentsAdapter(private val attachments: List<String>) : RecyclerView.Adapter<AttachmentsAdapter.AttachmentViewHolder>() {

        class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.attachment_image_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attachment, parent, false)
            return AttachmentViewHolder(view)
        }

        override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
            val attachment = attachments[position]
            Glide.with(holder.imageView.context).load(attachment).into(holder.imageView)

        }

        override fun getItemCount() = attachments.size
    }

}