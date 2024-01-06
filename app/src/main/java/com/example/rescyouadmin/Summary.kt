package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityReportsBinding
import com.example.rescyouadmin.databinding.ActivitySummaryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Summary : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BACK BUTTON
        binding.backButton.setOnClickListener {
            val intent = Intent(this, Reports::class.java)
            startActivity(intent)
            finish()
        }

        //SUMMARY - TOTAL PINS
        summaryTotalPins()

        //SUMMARY - TOTAL RESOLVED PINS
        summaryResolvedPins()

        //SUMMARY - MOST VULNERABLE SITIO
        summaryMostVulnerableSitio()

        //SUMMARY - FREQUENT DISASTER TYPES
        summaryFrequentDisasterTypes()

    }

    private fun summaryFrequentDisasterTypes() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")
        val disasterReference: DatabaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Disasters")

        val disasterNames = mutableListOf<String>()

        // Retrieve the list of sitioName from the Sitios node
        disasterReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val disasterName = snapshot.child("disasterName").getValue(String::class.java)
                    if (disasterName != null) {
                        disasterNames.add(disasterName)
                    }
                }

                // Now that sitioNames is populated, start listening for changes in the Pins node
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val disasterCountMap = HashMap<String, Int>()
                        for (snapshot in dataSnapshot.children) {
                            val disaster = snapshot.child("disasterType").getValue(String::class.java)
                            if (disaster != null && disaster in disasterNames) {
                                val count = disasterCountMap[disaster] ?: 0
                                disasterCountMap[disaster] = count + 1
                            }
                        }

                        // Find the sitios with the maximum count
                        var maxCount = 0
                        val mostFrequentDisaster = mutableListOf<String>()
                        for ((sitio, count) in disasterCountMap) {
                            if (count > maxCount) {
                                mostFrequentDisaster.clear()
                                mostFrequentDisaster.add(sitio)
                                maxCount = count
                            } else if (count == maxCount) {
                                mostFrequentDisaster.add(sitio)
                            }
                        }

                        binding.disasterTypeNameTextview.text = mostFrequentDisaster.joinToString(", ")
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

    private fun summaryMostVulnerableSitio() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")
        val sitiosReference: DatabaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Sitios")

        val sitioNames = mutableListOf<String>()

        // Retrieve the list of sitioName from the Sitios node
        sitiosReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val sitioName = snapshot.child("sitioName").getValue(String::class.java)
                    if (sitioName != null) {
                        sitioNames.add(sitioName)
                    }
                }

                // Now that sitioNames is populated, start listening for changes in the Pins node
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val sitioCountMap = HashMap<String, Int>()
                        for (snapshot in dataSnapshot.children) {
                            val sitio = snapshot.child("sitio").getValue(String::class.java)
                            if (sitio != null && sitio in sitioNames) {
                                val count = sitioCountMap[sitio] ?: 0
                                sitioCountMap[sitio] = count + 1
                            }
                        }

                        // Find the sitios with the maximum count
                        var maxCount = 0
                        val mostVulnerableSitios = mutableListOf<String>()
                        for ((sitio, count) in sitioCountMap) {
                            if (count > maxCount) {
                                mostVulnerableSitios.clear()
                                mostVulnerableSitios.add(sitio)
                                maxCount = count
                            } else if (count == maxCount) {
                                mostVulnerableSitios.add(sitio)
                            }
                        }

                        binding.sitioTextview.text = mostVulnerableSitios.joinToString(", ")
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

    private fun summaryResolvedPins() {
//        binding.resolvedpinsNameTextview.text = "Resolved Pins"

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var resolvedPinsCount = 0
                for (snapshot in dataSnapshot.children) {
                    val resolved = snapshot.child("resolved").getValue(String::class.java)?.toBoolean()
                    if (resolved == true) {
                        resolvedPinsCount++
                    }
                }
                binding.resolvedpinsTextview.text = resolvedPinsCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun summaryTotalPins() {
//        binding.nameTextview.text = "Total Pins"

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Pins")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val totalPins = dataSnapshot.childrenCount
                binding.summaryTotalPinsTextview.text = totalPins.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}