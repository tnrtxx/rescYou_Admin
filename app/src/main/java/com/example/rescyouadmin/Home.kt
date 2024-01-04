package com.example.rescyouadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rescyouadmin.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //PREPAREDNESS TIPS
        binding.tipsButton.setOnClickListener {
            val intent = Intent(this, PreparednessTips::class.java)
            startActivity(intent)
        }

        //Hotlines
        binding.hotlinesButton.setOnClickListener {
            val intent = Intent(this, Hotlines::class.java)
            startActivity(intent)
        }

        //Evacuation Centers
        binding.centersButton.setOnClickListener {
            val intent = Intent(this, EvacuationCenters::class.java)
            startActivity(intent)
        }

        //BOTTOM NAV VIEW
        // Initialize and assign variable
        var bottomNavigationView = binding.bottomNavView
        binding.bottomNavView.selectedItemId = R.id.info

        // Initialize and assign variable
        val selectedItem = bottomNavigationView.selectedItemId

        bottomNavigationView.setOnNavigationItemSelectedListener(navBarWhenClicked)
    }

    //NAV BAR
    private val navBarWhenClicked = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.info -> {
                if (this !is Home) {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }

                return@OnNavigationItemSelectedListener true
            }

            R.id.reports -> {
                val intent = Intent(this, Reports::class.java)
                startActivity(intent)
                finish()  // Finish the current activity
                return@OnNavigationItemSelectedListener true
            }


            R.id.profile -> {
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
                finish()  // Finish the current activity
                return@OnNavigationItemSelectedListener true


            }
        }
        return@OnNavigationItemSelectedListener false
    }
}