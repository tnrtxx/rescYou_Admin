package com.example.rescyouadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.textfield.TextInputLayout
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class HotlinesEdit : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var serviceTextInputLayout: TextInputLayout
    private lateinit var hotlineNumTextInputLayout: TextInputLayout
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var dataId: String // Assuming you pass the data ID through an intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotlines_edit)

        databaseReference = FirebaseDatabase.getInstance().reference

        // Assuming you pass the data ID through an intent
        dataId = intent.getStringExtra("dataId").toString()

        serviceTextInputLayout = findViewById(R.id.serviceTextLayout)
        hotlineNumTextInputLayout = findViewById(R.id.serviceHotlineNumTextLayout)

        saveButton = findViewById(R.id.saveHotlineButton)
        cancelButton = findViewById(R.id.cancelHotlineButton)

        // Load existing data from Firebase
        loadDataFromFirebase()

        val backButton: ImageView = findViewById(R.id.back_button)


        backButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to go back?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(this, Hotlines::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        saveButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to save the changes?")
                .setPositiveButton("Yes") { _, _ ->
                    editHotline()
                }
                .setNegativeButton("No", null)
                .show()
        }

        cancelButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to cancel?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(this, Hotlines::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun loadDataFromFirebase() {
        databaseReference.child("Hotlines").child(dataId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val dataName = snapshot.child("dataName").getValue(String::class.java)
                    val dataPhone = snapshot.child("dataPhone").getValue(String::class.java)

                    serviceTextInputLayout.editText?.setText(dataName)
                    hotlineNumTextInputLayout.editText?.setText(dataPhone)
                }
            }
            .addOnFailureListener { exception ->
                showToast("Error loading data: ${exception.message}")
            }
    }

    private fun editHotline() {
        val newDataName = serviceTextInputLayout.editText?.text.toString().trim()
        val newDataPhone = hotlineNumTextInputLayout.editText?.text.toString().trim()

        if (newDataName.isNotEmpty() && newDataPhone.isNotEmpty()) {
            val dataItemReference = databaseReference.child("Hotlines").child(dataId)

            val updatedData = mapOf(
                "dataName" to newDataName,
                "dataPhone" to newDataPhone
            )

            dataItemReference.updateChildren(updatedData)
                .addOnSuccessListener {
                    showToast("Hotline updated successfully.")
                    finish()
                }
                .addOnFailureListener {
                    showToast("Failed to update Hotline. Error: ${it.message}")
                }
        } else {
            showToast("Please fill in all fields")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Create an Intent to redirect to your desired activity
        val intent = Intent(this, Hotlines::class.java)

        // Start the new activity
        startActivity(intent)

        // Finish the current activity to prevent going back to it when pressing back
        finish()
    }
}
