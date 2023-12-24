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
import com.example.rescyouadmin.databinding.ActivityEvacuationCenterAddEditBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "EvacuationCenterAdd"

class EvacuationCenterAddEdit : AppCompatActivity() {

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 9001
    }

    private lateinit var binding: ActivityEvacuationCenterAddEditBinding
    private lateinit var data: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    // Variable Declarations
    private var id: String? = null
    private var name: String? = null
    private var address: String? = null
    private var latitude: String? = null
    private var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCenterAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeUI()
    }

    private fun initializeUI() {
        setupBackButton()
        setupPlacesAutocomplete()
        setupStatusDropdown()

        binding.saveEvacuationCenterButton.setOnClickListener {
            saveEvacuationCenter()
        }

        binding.cancelEvacuationCenterButton.setOnClickListener {
            navigateToEvacuationCenters()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            navigateToEvacuationCenters()
        }
    }

    private fun setupPlacesAutocomplete() {
        Places.initialize(applicationContext, getString(R.string.api_key))
        binding.nameTextInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startAutocompleteActivity()
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

    private fun saveEvacuationCenter() {
        val nameInput = binding.nameTextInput.text.toString().trim()
        val inChargeInput = binding.inChargeTextInput.text.toString().trim()
        val contactNumInput = binding.inChargeContactNumTextInput.text.toString().trim()
        val occupantsInput = binding.occupantsTextInput.text.toString().trim()
        val statusSelected = binding.statusDropdown.text.toString().trim()

        if (isValidInput(
                nameInput,
                inChargeInput,
                contactNumInput,
                occupantsInput,
                statusSelected
            )
        ) {
            data = FirebaseDatabase.getInstance()
            databaseReference = data.getReference("Evacuation Centers")

            // Check if the ID already exists in the database
            databaseReference.orderByChild("id").equalTo(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            showErrorAndFocus(binding.nameTextInput, "This evacuation center is already listed. Please choose another to avoid duplication.")
                        } else {
                            // ID doesn't exist, proceed to save the data
                            val evacuationCenterData = EvacuationCenterData(
                                id, name, address, latitude, longitude, statusSelected,
                                inChargeInput, contactNumInput, occupantsInput
                            )
                            val evacuationCenterRef = databaseReference.push()
                            evacuationCenterRef.setValue(evacuationCenterData)
                            showToast("Evacuation Center added successfully.")
                            navigateToEvacuationCenters()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Database error: ${error.message}")
                    }
                })
        }
    }

    private fun isValidInput(
        nameInput: String,
        inChargeInput: String,
        contactNumInput: String,
        occupantsInput: String,
        statusSelected: String
    ): Boolean {
        return when {
            nameInput.isEmpty() -> {
                showErrorAndFocus(binding.nameTextInput, "Please select a place")
                false
            }

            inChargeInput.isEmpty() -> {
                showErrorAndFocus(
                    binding.inChargeTextInput,
                    "Please input the name of the person in charge"
                )
                false
            }

            contactNumInput.isEmpty() -> {
                showErrorAndFocus(
                    binding.inChargeContactNumTextInput,
                    "Please input the contact number of the person in charge"
                )
                false
            }

            occupantsInput.isEmpty() -> {
                showErrorAndFocus(binding.occupantsTextInput, "Please input the sitio occupants")
                false
            }

            statusSelected.isEmpty() -> {
                showErrorAndFocus(
                    binding.statusDropdown,
                    "Please select the status of the evacuation center"
                )
                false
            }

            else -> true
        }
    }

    private fun showErrorAndFocus(view: EditText, errorMessage: String) {
        view.error = errorMessage
        view.requestFocus()
    }

    private fun navigateToEvacuationCenters() {
        startActivity(Intent(this, EvacuationCenters::class.java))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    id = place.id
                    name = place.name
                    address = place.address
                    longitude = place.latLng?.longitude.toString()
                    latitude = place.latLng?.latitude.toString()
                    binding.nameTextInput.setText(place.name)

                    // Clear the error for nameTextInput
                    binding.nameTextInput.error = null

                    Log.i(TAG, "Place: $id, $name, $address, $longitude, $latitude")
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Snackbar.make(binding.root, status.statusMessage!!, Snackbar.LENGTH_SHORT)
                        .show()

                    // Set the error for nameTextInput
                    showErrorAndFocus(binding.nameTextInput, "Please select a place")
                }

                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "The user cancelled the operation.")
                }
            }
        }
    }
}
