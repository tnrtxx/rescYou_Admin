package com.example.rescyouadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.textfield.TextInputLayout

class HotlineAdd : AppCompatActivity() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotlines_add)

        val saveButton: Button = findViewById(R.id.saveHotlineButton)
        val cancelButton: Button = findViewById(R.id.cancelHotlineButton)
        val backButton: ImageView = findViewById(R.id.back_button)


        // BACK BUTTON
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
                .setMessage("Are you sure you want to save?")
                .setPositiveButton("Yes") { _, _ ->
                    addHotline()
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

    private fun addHotline() {
        val serviceTextInputLayout: TextInputLayout = findViewById(R.id.serviceTextLayout)
        val hotlineNumTextInputLayout: TextInputLayout = findViewById(R.id.serviceHotlineNumTextLayout)

        val newDataName = serviceTextInputLayout.editText?.text.toString().trim()
        val newDataPhone = hotlineNumTextInputLayout.editText?.text.toString().trim()

        if (newDataName.isNotEmpty() && newDataPhone.isNotEmpty()) {
            addHotlineData(newDataName, newDataPhone)
        } else {
            showToast("Please fill in all fields")
        }
    }

    private fun addHotlineData(newDataName: String, newDataPhone: String) {
        val hotlinesReference = databaseReference.child("Hotlines")
        val newDataKey = hotlinesReference.push().key

        val newData = mapOf(
            "dataName" to newDataName,
            "dataPhone" to newDataPhone
        )

        hotlinesReference.child(newDataKey ?: "").setValue(newData)
            .addOnSuccessListener {
                showToast("Hotline added successfully")
                finish()
            }
            .addOnFailureListener {
                showToast("Failed to add Hotline. Error: ${it.message}")
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