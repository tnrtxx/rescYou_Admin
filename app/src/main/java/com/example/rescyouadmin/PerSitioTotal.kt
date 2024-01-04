package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityPerSitioTotalBinding
import com.example.rescyouadmin.databinding.ActivityResolvedPinsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerSitioTotal : AppCompatActivity() {

    private lateinit var binding: ActivityPerSitioTotalBinding
    private lateinit var perSitioList: MutableList<SitioDataClass>
        private lateinit var adapter: PerSitioAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener

    private lateinit var sitioReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerSitioTotalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BACK BUTTON
        binding.backButton.setOnClickListener {
            finish()
        }

        //ACTIVATE THE RECYCLERVIEW
        recyclerView= findViewById(R.id.per_sitio_recycler_view)
        perSitioList = mutableListOf() // Initialize dataList here

        // Initialize adapter here
        adapter = PerSitioAdapter(perSitioList)
        recyclerView.adapter = adapter

        val gridLayoutManager = GridLayoutManager(this, 1)
        this.recyclerView.layoutManager = gridLayoutManager


        readDataFromFirebase()


    }

    private fun readDataFromFirebase() {
        val sitiosReference = FirebaseDatabase.getInstance().getReference("Sitios")
        val pinsReference = FirebaseDatabase.getInstance().getReference("Pins")

        sitiosReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                perSitioList.clear()
                for (ds in dataSnapshot.children) {
                    val sitio = ds.getValue(SitioDataClass::class.java)
                    if (sitio != null) {
                        perSitioList.add(sitio)
                    }
                }

                pinsReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Reset total count for each sitio
                        for (sitio in perSitioList) {
                            sitio.total = 0
                        }

                        for (ds in dataSnapshot.children) {
                            val pinSitio = ds.child("sitio").getValue(String::class.java)
                            val sitio = perSitioList.find { it.sitioName == pinSitio }
                            sitio?.total = sitio?.total?.plus(1) ?: 0
                        }
                        adapter.notifyDataSetChanged()

                        // Update total_resolved_pins TextView
                        val totalSitioTextView: TextView = findViewById(R.id.total_sitio)
                        totalSitioTextView.text = "Total Sitios: ${perSitioList.size}"
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