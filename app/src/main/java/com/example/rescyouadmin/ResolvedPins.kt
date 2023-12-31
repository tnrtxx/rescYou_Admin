package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityResolvedPinsBinding
import com.example.rescyouadmin.databinding.ActivitySignUpBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResolvedPins : AppCompatActivity() {

    private lateinit var binding: ActivityResolvedPinsBinding
    private lateinit var resolvedList: MutableList<PinDataClass>
    private lateinit var adapter: ResolvedPinsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener

    private val database = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResolvedPinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BACK BUTTON
        binding.backButton.setOnClickListener {
            val intent = Intent(this, Reports::class.java)
            startActivity(intent)
        }

        //ACTIVATE THE RECYCLERVIEW
        recyclerView= findViewById(R.id.resolvedpins_recycler_view)
        resolvedList = mutableListOf() // Initialize dataList here

        // Initialize adapter here
        adapter = ResolvedPinsAdapter(resolvedList)
        recyclerView.adapter = adapter

        val gridLayoutManager = GridLayoutManager(this, 1)
        this.recyclerView.layoutManager = gridLayoutManager

        databaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")

        eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resolvedList.clear()
                for (itemSnapshot in snapshot.children) {
                    val pinDataClass = itemSnapshot.getValue(PinDataClass::class.java)
                    if (pinDataClass != null) {
                        pinDataClass.key = itemSnapshot.key
                        resolvedList.add(pinDataClass)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}