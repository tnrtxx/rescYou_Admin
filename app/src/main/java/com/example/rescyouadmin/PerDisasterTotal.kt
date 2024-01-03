package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityPerDisasterTotalBinding
import com.example.rescyouadmin.databinding.ActivityPerSitioTotalBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerDisasterTotal : AppCompatActivity() {

    private lateinit var binding: ActivityPerDisasterTotalBinding
    private lateinit var perDisasterList: MutableList<PerDisasterTotalDataClass>
    private lateinit var adapter: PerDisasterTotalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerDisasterTotalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BACK BUTTON
        binding.backButton.setOnClickListener {
            val intent = Intent(this, Reports::class.java)
            startActivity(intent)
        }

        // ACTIVATE THE RECYCLERVIEW
        recyclerView = findViewById(R.id.per_disaster_total_recycler_view)
        perDisasterList = mutableListOf() // Initialize dataList here

        // Initialize adapter here
        adapter = PerDisasterTotalAdapter(perDisasterList)
        recyclerView.adapter = adapter

        val gridLayoutManager = GridLayoutManager(this, 1)
        this.recyclerView.layoutManager = gridLayoutManager

        readDataFromFirebase()
    }

    private fun readDataFromFirebase() {
        val disastersReference = FirebaseDatabase.getInstance().getReference("Disasters")
        val pinsReference = FirebaseDatabase.getInstance().getReference("Pins")

        disastersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                perDisasterList.clear()
                for (ds in dataSnapshot.children) {
                    val disaster = ds.getValue(PerDisasterTotalDataClass::class.java)
                    if (disaster != null) {
                        perDisasterList.add(disaster)
                    }
                }

                pinsReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Reset total count for each sitio
                        for (disaster in perDisasterList) {
                            disaster.total = 0
                        }

                        for (ds in dataSnapshot.children) {
                            val pinDisaster = ds.child("disasterType").getValue(String::class.java)
                            val sitio = perDisasterList.find { it.disasterName == pinDisaster }
                            sitio?.total = sitio?.total?.plus(1) ?: 0
                        }
                        adapter.notifyDataSetChanged()

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle possible errors.
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
    }
