package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rescyouadmin.databinding.ActivityHomeBinding
import com.example.rescyouadmin.databinding.ActivityReportsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Reports : AppCompatActivity() {
    private lateinit var binding: ActivityReportsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //RESOLVED PINS BUTTON
        binding.resolvedpinsButton.setOnClickListener {
            val intent = Intent(this, ResolvedPins::class.java)
            startActivity(intent)
        }

        //BOTTOM NAV VIEW
        // Initialize and assign variable
        var bottomNavigationView = binding.bottomNavView
        binding.bottomNavView.selectedItemId = R.id.reports

        // Initialize and assign variable
        val selectedItem = bottomNavigationView.selectedItemId
        // Toast.makeText(applicationContext, selectedItem.toString(), Toast.LENGTH_SHORT).show()

        bottomNavigationView.setOnNavigationItemSelectedListener(navBarWhenClicked)
    }

    //NAV BAR
    private val navBarWhenClicked = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.info -> {
//                Toast.makeText(applicationContext, "information", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.reports -> {
//                Toast.makeText(applicationContext, "information", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Reports::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }


            R.id.profile -> {
//                Toast.makeText(applicationContext, "profile", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true


            }
        }
        return@OnNavigationItemSelectedListener false
    }
}
