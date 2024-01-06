package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityEvacuationCentersBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * Evacuation Centers (Admin)
 * Activity for displaying and managing evacuation centers.
 */


private const val TAG = "EvacuationCenters"

class EvacuationCenters : AppCompatActivity() {

    private lateinit var binding: ActivityEvacuationCentersBinding  // ViewBinding variable
    private lateinit var databaseReference: DatabaseReference       // Firebase-related variable

    // RecyclerView-related variables
    private lateinit var evacuationCenterRecyclerView: RecyclerView
    private lateinit var evacuationCenterAdapter: EvacuationCenterAdapter
    private lateinit var evacuationCenterArrayList: ArrayList<EvacuationCenterData>

    // Shimmer-related variables
    private lateinit var shimmerLayout: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCentersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shimmerLayout = binding.shimmerLayout
        initializeUI()                  // Initialize UI components
        initializeRecyclerView()        // Initialize RecyclerView and its adapter
        shimmerLayout.startShimmer()    // Start shimmer animation
        getEvacuationCenterData()       // Fetch and display evacuation center data
    }

    /*** Helper Functions ***/

    private fun initializeUI() {

        // Back Button: Navigate to the Home activity
        binding.backButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        // Add New Evacuation Center Button: Navigate to the EvacuationCenterAdd activity for adding a new evacuation center
        binding.fabAddEvacuationCenter.setOnClickListener {
            val intent = Intent(this, EvacuationCenterAdd::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeRecyclerView() {
        evacuationCenterRecyclerView = binding.evacuationCenterRecyclerView
        evacuationCenterRecyclerView.layoutManager = LinearLayoutManager(this)
        evacuationCenterRecyclerView.setHasFixedSize(true)

        evacuationCenterArrayList = arrayListOf()
        evacuationCenterAdapter = EvacuationCenterAdapter(evacuationCenterArrayList)
        evacuationCenterRecyclerView.adapter = evacuationCenterAdapter
    }

    private fun getEvacuationCenterData() {

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Evacuation Centers")
        databaseReference.keepSynced(true) //add this line of code after nung Firebase get instance para sa mga page na need ioffline.

        // Event listener for data changes
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    evacuationCenterArrayList.clear()
                    for (evacuationCenterSnapshot in snapshot.children) {
                        val evacuationCenter =
                            evacuationCenterSnapshot.getValue(EvacuationCenterData::class.java)
                        evacuationCenterArrayList.add(evacuationCenter!!)
                    }
                    evacuationCenterArrayList.sortBy { it.name }
                    evacuationCenterAdapter.notifyDataSetChanged()

                    // Stop shimmer animation after fetching data
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = ShimmerFrameLayout.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database operation cancellation
                Log.e(TAG, "Database operation cancelled: ${error.message}")
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Create an Intent to redirect to your desired activity
        val intent = Intent(this, Home::class.java)

        // Start the new activity
        startActivity(intent)

        // Finish the current activity to prevent going back to it when pressing back
        finish()
    }
}
