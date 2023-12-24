package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityEvacuationCentersBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EvacuationCenters : AppCompatActivity() {

    private lateinit var binding: ActivityEvacuationCentersBinding

    private lateinit var databaseReference: DatabaseReference
    private lateinit var evacuationCenterRecyclerView: RecyclerView
    private lateinit var evacuationCenterAdapter: EvacuationCenterAdapter
    private lateinit var evacuationCenterArrayList: ArrayList<EvacuationCenterData>

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
            val intent = Intent(this, EvacuationCenterAddEdit::class.java)
            startActivity(intent)
        }


        //RECYCLER VIEW
        evacuationCenterRecyclerView = binding.evacuationCenterRecyclerView
        evacuationCenterRecyclerView.layoutManager = LinearLayoutManager(this)
        evacuationCenterRecyclerView.setHasFixedSize(true)

        evacuationCenterArrayList = arrayListOf()
        evacuationCenterAdapter = EvacuationCenterAdapter(evacuationCenterArrayList)
        evacuationCenterRecyclerView.adapter = evacuationCenterAdapter

        getUserData()
    }

    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Evacuation Centers")
        databaseReference.keepSynced(true)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    evacuationCenterArrayList.clear()
                    for (evacuationCenterSnapshot in snapshot.children) {
                        val evacuationCenter = evacuationCenterSnapshot.getValue(EvacuationCenterData::class.java)
                        evacuationCenterArrayList.add(evacuationCenter!!)
                    }
                    evacuationCenterAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EvacuationCenters", "Database operation cancelled: ${error.message}")
            }
        })
    }
}