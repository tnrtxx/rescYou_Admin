package com.example.rescyouadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityShowAllPinsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "ShowAllPins"
class ShowAllPins : AppCompatActivity() {


    private lateinit var binding: ActivityShowAllPinsBinding
    private lateinit var allPinsList: MutableList<PinDataClass>
    private lateinit var adapter: ShowAllPinsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener

    private val database = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowAllPinsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BACK BUTTON
        binding.backButton.setOnClickListener {
            finish()
        }

        //ACTIVATE THE RECYCLERVIEW
        recyclerView= findViewById(R.id.recycler_view)
        allPinsList = mutableListOf() // Initialize dataList here

        // Initialize adapter here
        adapter = ShowAllPinsAdapter(allPinsList)
        recyclerView.adapter = adapter

        val gridLayoutManager = GridLayoutManager(this, 1)
        this.recyclerView.layoutManager = gridLayoutManager

        databaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")

        eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allPinsList.clear()
                for (itemSnapshot in snapshot.children) {
                    val pinDataClass = itemSnapshot.getValue(PinDataClass::class.java)
                    if (pinDataClass != null) {
                        pinDataClass.key = itemSnapshot.key
                        allPinsList.add(pinDataClass)
                    }
                }
                adapter.notifyDataSetChanged()

                // Update total_resolved_pins TextView
               val totalPins: TextView = findViewById(R.id.total_pins)
               totalPins.text = "Total Pins: ${allPinsList.size}"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}