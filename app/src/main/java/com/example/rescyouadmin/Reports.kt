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

        //SHOW ALL PINS BUTTON
        binding.allpinsButton.setOnClickListener {
            val intent = Intent(this, ShowAllPins::class.java)
            startActivity(intent)
        }

        //RESOLVED PINS BUTTON
        binding.resolvedpinsButton.setOnClickListener {
            val intent = Intent(this, ResolvedPins::class.java)
            startActivity(intent)
        }

        //PER SITIO TOTAL BUTTON
        binding.persitioButton.setOnClickListener {
            val intent = Intent(this, PerSitioTotal::class.java)
            startActivity(intent)
        }

        //PER DISTRICT TOTAL BUTTON
        binding.perdisasterButton.setOnClickListener {
            val intent = Intent(this, PerDisasterTotal::class.java)
            startActivity(intent)
        }

        //SUMMARY BUTTON
        binding.summaryButton.setOnClickListener {
            val intent = Intent(this, Summary::class.java)
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
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.reports -> {
                if (this !is Reports) {
                    val intent = Intent(this, Reports::class.java)
                    startActivity(intent)
                    finish()
                }

                return@OnNavigationItemSelectedListener true
            }


            R.id.profile -> {
//                Toast.makeText(applicationContext, "profile", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true


            }
        }
        return@OnNavigationItemSelectedListener false
    }
}
