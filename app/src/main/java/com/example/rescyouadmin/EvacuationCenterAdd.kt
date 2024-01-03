package com.example.rescyouadmin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rescyouadmin.databinding.ActivityEvacuationCenterAddBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.checkerframework.checker.units.qual.C

/**
 * EvacuationCenterAdd
 * This activity allows the user to add a new evacuation center to the database.
 */

private const val TAG = "EvacuationCenterAdd"

class EvacuationCenterAdd : AppCompatActivity() {

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 9001
    }

    private lateinit var binding: ActivityEvacuationCenterAddBinding
    private lateinit var databaseReference: DatabaseReference

    // Variable Declarations
    private var evacuationCenterId: String? = null
    private var placeId: String? = null
    private var name: String? = null
    private var address: String? = null
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCenterAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI() // Initialize UI components
    }

    private fun initializeUI() {
        binding.saveEvacuationCenterButton.setOnClickListener {
            saveEvacuationCenter() // Save the evacuation center to the database
        }

        binding.backButton.setOnClickListener {
            finish() // Close the activity
        }

        binding.cancelEvacuationCenterButton.setOnClickListener {
            finish() // Close the activity
        }

        setupPlacesAutocomplete() // Setup Places Autocomplete
        setupStatusDropdown() // Setup Status Dropdown
    }

    /*** Helper Functions ***/

    private fun setupPlacesAutocomplete() {
        Places.initialize(applicationContext, Constants.GOOGLE_MAPS_API_KEY)

        binding.nameTextInput.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startAutocompleteActivity() // Start the Places Autocomplete activity
                view.performClick() // Perform a click to clear the focus
            }
            true
        }
    }

    private fun startAutocompleteActivity() {
        val canlubangBounds = RectangularBounds.newInstance(
            LatLng(14.2049, 121.0910),
            LatLng(14.2110, 121.0975)
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        )
            .setLocationBias(canlubangBounds)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setupStatusDropdown() {
        val status = resources.getStringArray(R.array.status_evacuation_center)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, status)
        binding.statusDropdown.setAdapter(arrayAdapter)
    }

    private fun isValidInput(vararg inputFields: Pair<String, EditText>): Boolean {
        inputFields.forEach { (input, view) ->
            if (input.isEmpty()) {
                showErrorAndFocus(view, "Please provide a valid input")
                return false
            }
        }
        return true
    }

    private fun showErrorAndFocus(view: EditText, errorMessage: String) {
        view.error = errorMessage
        view.requestFocus()
    }

    private fun saveEvacuationCenter() {
        val nameInput = binding.nameTextInput.text.toString().trim()
        val occupantsInput = binding.occupantsTextInput.text.toString().trim()
        val statusSelected = binding.statusDropdown.text.toString().trim()

        // Check if the input fields are valid
        if (isValidInput(
                nameInput to binding.nameTextInput,
                occupantsInput to binding.occupantsTextInput,
                statusSelected to binding.statusDropdown
            )
        ) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Evacuation Centers")

            evacuationCenterId =
                databaseReference.push().key // Use push() to generate a unique key

            // Check if the inputted evacuation center already exists in the database
            databaseReference.orderByChild("placeId").equalTo(placeId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            showErrorAndFocus(
                                binding.nameTextInput,
                                "This evacuation center is already listed. Please choose another to avoid duplication."
                            )
                        } else {
                            // If the place doesn't already exist in the database, save the it to the database

                            val evacuationCenterData = EvacuationCenterData(
                                // !! Make sure that they are sorted in the same order as the constructor of EvacuationCenterData (data class/model) !!
                                evacuationCenterId, placeId, name, address, latitude, longitude,
                                statusSelected, occupantsInput
                            )

                            databaseReference.child(evacuationCenterId!!).setValue(evacuationCenterData)

                                .addOnSuccessListener{
                                    Toast.makeText(this@EvacuationCenterAdd, "$name was added successfully.",
                                        Toast.LENGTH_SHORT).show() // Evacuation center has been added successfully
                                    clearInputFields()
                                }

                                .addOnFailureListener {
                                    Toast.makeText(this@EvacuationCenterAdd, "An error has occurred while adding $name.",
                                        Toast.LENGTH_SHORT).show()  // Evacuation center failed to be added
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Database error: ${error.message}")
                    }
                })
        }
    }

    private fun clearInputFields() {
        binding.nameTextInput.text.clear()
        binding.occupantsTextInput.text?.clear()
        binding.statusDropdown.text.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            handleAutocompleteActivityResult(resultCode, data)
        }
    }

    private fun handleAutocompleteActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                updatePlaceData(place)
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }

            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "The user cancelled the operation.")
            }
        }
    }

    private fun updatePlaceData(place: Place) {
        placeId = place.id
        name = place.name
        address = place.address
        longitude = place.latLng?.longitude
        latitude = place.latLng?.latitude
        binding.nameTextInput.setText(place.name)

        // Clear the error for nameTextInput
        binding.nameTextInput.error = null

        // Logs the data of the place that was selected from Google Places Autocomplete widget
        // !! This is for debugging purposes only!!
        // TODO: Remove this later
        Log.d(TAG, "Place ID: $placeId, $name, $address, $longitude, $latitude")
    }

}



