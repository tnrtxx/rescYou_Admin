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
import com.example.rescyouadmin.databinding.ActivityEvacuationCenterEditBinding
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

/**
 * EvacuationCenterEdit
 * This activity allows the user to edit an existing evacuation center's details in the database.
 */

// TODO: In updating the evacuation center. Check first if the id inputted name of place already exist in the database.

private const val TAG = "EvacuationCenterEdit"

class EvacuationCenterEdit : AppCompatActivity() {

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 9001
    }

    private lateinit var binding: ActivityEvacuationCenterEditBinding
    private lateinit var databaseReference: DatabaseReference

    // Variable Declarations
    private var evacuationCenterId: String? = null
    private var updatedPlaceId: String? = null
    private var updatedName: String? = null
    private var updatedAddress: String? = null
    private var updatedLatitude: Double? = 0.0
    private var updatedLongitude: Double? = 0.0
    private var updatedInCharge: String? = null
    private var updatedInChargeContactNum: String? = null
    private var updatedOccupants: String? = null
    private var updatedStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCenterEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
    }

    private fun initializeUI() {

        // Get data from intent
        evacuationCenterId = intent.getStringExtra("evacuationCenterId")
        updatedPlaceId = intent.getStringExtra("placeId")
        updatedName = intent.getStringExtra("name")
        updatedAddress = intent.getStringExtra("address")
        updatedLatitude = intent.getDoubleExtra("latitude", 0.0)
        updatedLongitude = intent.getDoubleExtra("longitude", 0.0)
        updatedInCharge = intent.getStringExtra("inCharge")
        updatedInChargeContactNum = intent.getStringExtra("inChargeContactNum")
        updatedOccupants = intent.getStringExtra("occupants")
        updatedStatus = intent.getStringExtra("status")

        // Set data to text fields
        binding.editNameTextInput.setText(updatedName)
        binding.editInChargeTextInput.setText(updatedInCharge)
        binding.editInChargeContactNumTextInput.setText(updatedInChargeContactNum)
        binding.editOccupantsTextInput.setText(updatedOccupants)
        binding.editStatusDropdown.setText(
            updatedStatus, false
        ) // Used "false" to prevent the dropdown from showing

        binding.editNameTextInput.error = null  // Clear the error for nameTextInput
        setupPlacesAutocomplete()               // Setup Places Autocomplete Functionality
        setupStatusDropdown()                   // Setup Status Dropdown

        binding.updateEvacuationCenterButton.setOnClickListener {
            updateEvacuationCenter() // Update the evacuation center to the database
        }

        binding.backButton.setOnClickListener {
            finish() // Finish the activity
        }

        binding.cancelEvacuationCenterButton.setOnClickListener {
            finish() // Finish the activity
        }
    }

    private fun setupPlacesAutocomplete() {
        Places.initialize(applicationContext, Constants.GOOGLE_MAPS_API_KEY)
        binding.editNameTextInput.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startAutocompleteActivity()
                view.performClick()
            }
            true
        }
    }

    private fun startAutocompleteActivity() {
        val canlubangBounds = RectangularBounds.newInstance(
            LatLng(14.2049, 121.0910), LatLng(14.2110, 121.0975)
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        ).setLocationBias(canlubangBounds).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun setupStatusDropdown() {
        val statusDropdown = resources.getStringArray(R.array.status_evacuation_center)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, statusDropdown)
        binding.editStatusDropdown.setAdapter(arrayAdapter)
        binding.editStatusDropdown.threshold =
            0 // Used to the threshold to 0 to show the dropdown with any number of characters
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

    private fun updateEvacuationCenter() {
        // Get the updated data from the text fields
        val updatedName = binding.editNameTextInput.text.toString().trim()
        val updatedInCharge = binding.editInChargeTextInput.text.toString().trim()
        val updatedInChargeContactNum =
            binding.editInChargeContactNumTextInput.text.toString().trim()
        val updatedOccupants = binding.editOccupantsTextInput.text.toString().trim()
        val updatedStatus = binding.editStatusDropdown.text.toString().trim()

        if (isValidInput(
                updatedName to binding.editNameTextInput,
                updatedInCharge to binding.editInChargeTextInput,
                updatedInChargeContactNum to binding.editInChargeContactNumTextInput,
                updatedOccupants to binding.editOccupantsTextInput,
                updatedStatus to binding.editStatusDropdown
            )
        ) {
            // Update the evacuation center data in the database
            databaseReference = FirebaseDatabase.getInstance().getReference("Evacuation Centers")

            // Find the existing evacuation center using the evacuationCenterId
            databaseReference.child(evacuationCenterId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // If the placeId is the same, update the data
                        if (snapshot.child("placeId").getValue(String::class.java) == updatedPlaceId) {
                            snapshot.ref.updateChildren(
                                mapOf(
                                    "name" to updatedName,
                                    "inCharge" to updatedInCharge,
                                    "inChargeContactNum" to updatedInChargeContactNum,
                                    "occupants" to updatedOccupants,
                                    "status" to updatedStatus
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(
                                    this@EvacuationCenterEdit,
                                    "Evacuation Center was edited successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@EvacuationCenterEdit,
                                    "An error has occurred while editing the evacuation center.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // If the placeId is different, show an error
                            showErrorAndFocus(
                                binding.editNameTextInput,
                                "This evacuation center is already listed. Please choose another to avoid duplication."
                            )
                        }
                    } else {
                        Log.e(TAG, "Evacuation center not found in the database.")
                    }
                }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Database error: ${error.message}")
                    }
                })
        }

        // Log the updated data
        // !! This is for debugging purposes only!!
        // TODO: Remove this later
        Log.i(
            TAG,
            "Place Id: $updatedPlaceId, Name: $updatedName, Status: $updatedStatus, InCharge: $updatedInCharge, " +
                    "InChargeContactNum: $updatedInChargeContactNum, Occupants: $updatedOccupants, " +
                    "Address: $updatedAddress, Latitude: $updatedLatitude, Longitude: $updatedLongitude"
        )


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
        updatedPlaceId = place.id
        updatedName = place.name
        updatedAddress = place.address
        updatedLongitude = place.latLng?.longitude
        updatedLatitude = place.latLng?.latitude
        binding.editNameTextInput.setText(place.name)

        // Clear the error for nameTextInput
        binding.editNameTextInput.error = null

        // Logs the data of the place that was selected from Google Places Autocomplete widget
        // !! This is for debugging purposes only!!
        // TODO: Remove this later
        Log.i(
            TAG,
            "Place Id: $updatedPlaceId, Name: $updatedName, Status: $updatedStatus, InCharge: $updatedInCharge, InChargeContactNum: $updatedInChargeContactNum, Occupants: $updatedOccupants, Address: $updatedAddress, Latitude: $updatedLatitude, Longitude: $updatedLongitude"
        )
    }

}
