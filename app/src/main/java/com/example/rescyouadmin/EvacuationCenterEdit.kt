package com.example.rescyouadmin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.rescyouadmin.databinding.ActivityEvacuationCenterEditBinding
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

private const val TAG = "EvacuationCenterEdit"

class EvacuationCenterEdit : AppCompatActivity() {

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 9001
    }

    private lateinit var binding: ActivityEvacuationCenterEditBinding
    private lateinit var databaseReference: DatabaseReference

    // Variable Declarations
    private var evacuationCenterId: String? = null
    var updatedPlaceId: String? = null
    var updatedName: String? = null
    var updatedAddress: String? = null
    var updatedLatitude: String? = null
    var updatedLongitude: String? = null
    var updatedInCharge: String? = null
    var updatedInChargeContactNum: String? = null
    var updatedOccupants: String? = null
    var updatedStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCenterEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
    }

    private fun initializeUI() {
        setupBackButton()
        setupPlacesAutocomplete()
        setupStatusDropdown()

        // Get data from intent
        evacuationCenterId = intent.getStringExtra("evacuationCenterId")
        updatedPlaceId = intent.getStringExtra("placeId")
        updatedName = intent.getStringExtra("name")
        updatedAddress = intent.getStringExtra("address")
        updatedLatitude = intent.getStringExtra("latitude")
        updatedLongitude = intent.getStringExtra("longitude")
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

        binding.updateEvacuationCenterButton.setOnClickListener {
            updateEvacuationCenter()
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

    private fun updateEvacuationCenter() {
        // Get the updated data from the text fields
        val updatedName = binding.editNameTextInput.text.toString().trim()
        val updatedInCharge = binding.editInChargeTextInput.text.toString().trim()
        val updatedInChargeContactNum = binding.editInChargeContactNumTextInput.text.toString().trim()
        val updatedOccupants = binding.editOccupantsTextInput.text.toString().trim()
        val updatedStatus = binding.editStatusDropdown.text.toString().trim()

        if (isValidInput(
                updatedName,
                updatedInCharge,
                updatedInChargeContactNum,
                updatedOccupants,
                updatedStatus
            )
        ) {
            // Update the evacuation center data in the database
            databaseReference = FirebaseDatabase.getInstance().getReference("Evacuation Centers")
            val evacuationCenter = EvacuationCenterData(
                evacuationCenterId,
                updatedPlaceId,
                updatedName,
                updatedAddress,
                updatedLatitude,
                updatedLongitude,
                updatedStatus,
                updatedInCharge,
                updatedInChargeContactNum,
                updatedOccupants
            )

            evacuationCenterId?.let { databaseReference.child(it).setValue(evacuationCenter) }
            databaseReference = FirebaseDatabase.getInstance().getReference("Evacuation Centers")
        }
        Log.i(
            TAG,
            "Place Id: $updatedPlaceId, Name: $updatedName, Status: $updatedStatus, InCharge: $updatedInCharge, InChargeContactNum: $updatedInChargeContactNum, Occupants: $updatedOccupants, Address: $updatedAddress, Latitude: $updatedLatitude, Longitude: $updatedLongitude"
        )

    }

    private fun isValidInput(
        updatedName: String,
        updatedInCharge: String,
        updatedInChargeContactNum: String,
        updatedOccupants: String,
        updatedStatus: String
    ): Boolean {
        return when {
            updatedName.isEmpty() -> {
                showErrorAndFocus(binding.editNameTextInput, "Please select a place")
                false
            }

            updatedInCharge.isEmpty() -> {
                showErrorAndFocus(
                    binding.editInChargeTextInput, "Please input the name of the person in charge"
                )
                false
            }

            updatedInChargeContactNum.isEmpty() -> {
                showErrorAndFocus(
                    binding.editInChargeContactNumTextInput,
                    "Please input the contact number of the person in charge"
                )
                false
            }

            updatedOccupants.isEmpty() -> {
                showErrorAndFocus(
                    binding.editOccupantsTextInput, "Please input the sitio occupants"
                )
                false
            }

            updatedStatus.isEmpty() -> {
                showErrorAndFocus(
                    binding.editStatusDropdown, "Please select the status of the evacuation center"
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

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToEvacuationCenters() {
        startActivity(Intent(this, EvacuationCenters::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    updatedPlaceId = place.id
                    updatedName = place.name
                    updatedAddress = place.address
                    updatedLongitude = place.latLng?.longitude.toString()
                    updatedLatitude = place.latLng?.latitude.toString()
                    binding.editNameTextInput.setText(place.name)

                    // Clear the error for nameTextInput
                    binding.editNameTextInput.error = null

                    Log.i(
                        TAG,
                        "Place Id: $updatedPlaceId, Name: $updatedName, Status: $updatedStatus, InCharge: $updatedInCharge, InChargeContactNum: $updatedInChargeContactNum, Occupants: $updatedOccupants, Address: $updatedAddress, Latitude: $updatedLatitude, Longitude: $updatedLongitude"
                    )
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Snackbar.make(binding.root, status.statusMessage!!, Snackbar.LENGTH_SHORT)
                        .show()

                    // Set the error for nameTextInput
                    showErrorAndFocus(binding.editNameTextInput, "Please select a place")
                }

                Activity.RESULT_CANCELED -> {
                    Log.i(TAG, "The user cancelled the operation.")
                }
            }
        }
    }
}
