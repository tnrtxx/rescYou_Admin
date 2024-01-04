package com.example.rescyouadmin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class HotlinesAdapter(private val hotlinesList: MutableList<HotlinesDataClass>) : RecyclerView.Adapter<HotlinesAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataNameTextView: TextView = itemView.findViewById(R.id.data_name_textview)
        val dataPhoneTextView: TextView = itemView.findViewById(R.id.data_phone_textview)
        val editButton: TextView = itemView.findViewById(R.id.editHotlineButton)
        val deleteButton: TextView = itemView.findViewById(R.id.deleteHotlineButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_hotlines, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = hotlinesList[position]
        holder.dataNameTextView.text = currentItem.dataName
        holder.dataPhoneTextView.text = currentItem.dataPhone

        // Handle click event to open HotlinesEdit
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, HotlinesEdit::class.java)
            intent.putExtra("dataId", currentItem.key) // Assuming 'key' is the unique identifier
            holder.itemView.context.startActivity(intent)
        }

        // Handle click event to dial the phone number
        holder.dataPhoneTextView.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:${currentItem.dataPhone}")
            holder.itemView.context.startActivity(dialIntent)
        }

        // Handle click event for editing
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, HotlinesEdit::class.java)
            intent.putExtra("dataId", currentItem.key) // Assuming 'key' is the unique identifier
            holder.itemView.context.startActivity(intent)
        }

        // Handle click event for deleting
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, position, currentItem.key)
        }
    }

    override fun getItemCount(): Int {
        return hotlinesList.size
    }

    private fun showDeleteConfirmationDialog(context: Context, position: Int, dataId: String?) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Hotline")
        alertDialogBuilder.setMessage("Are you sure you want to delete this hotline?")
        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface, _ ->
            deleteItem(position, dataId)
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun deleteItem(position: Int, dataId: String?) {
        // Remove the item from the list
        hotlinesList.removeAt(position)
        notifyItemRemoved(position)

        // Delete from Firebase
        dataId?.let {
            deleteFromFirebase(it)
        }
    }

    private fun deleteFromFirebase(dataId: String?) {
        dataId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Hotlines")
            val itemReference = databaseReference.child(it)

            itemReference.removeValue()
                .addOnSuccessListener {
                    Log.d("HotlinesAdapter", "Item deleted from Firebase successfully")
                }
                .addOnFailureListener {
                    Log.e("HotlinesAdapter", "Error deleting item from Firebase: ${it.message}")
                }
        }
    }
}