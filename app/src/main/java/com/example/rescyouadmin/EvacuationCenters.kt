package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityEvacuationCentersBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EvacuationCenters : AppCompatActivity() {

    private lateinit var binding: ActivityEvacuationCentersBinding

    private lateinit var data: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
//    private lateinit var adapter: DisasterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCentersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BACK BUTTON
        binding.backButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //ADD EVACUATION CENTER BUTTON
        binding.fabAddEvacuationCenter.setOnClickListener {
            val intent = Intent(this, EvacuationCenterAdd::class.java)
            startActivity(intent)
        }

        //RECYCLER VIEW


    }
}