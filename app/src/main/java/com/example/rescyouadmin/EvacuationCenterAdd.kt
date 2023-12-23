package com.example.rescyouadmin

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rescyouadmin.databinding.ActivityEvacuationCenterAddBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "EvacuationCenterAdd"
class EvacuationCenterAdd : AppCompatActivity() {

    private val AUTOCOMPLETE_REQUEST_CODE = 9001

    private lateinit var binding: ActivityEvacuationCenterAddBinding
    private lateinit var data: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
//  private lateinit var adapter: DisasterAdapter
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment

    // Variable Declarations
    var name: String? = null
    var address: String? = null
    var latitude: String? = null
    var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvacuationCenterAddBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Back Button
        binding.backButton.setOnClickListener {
            val intent = Intent(this, EvacuationCenters::class.java)
            startActivity(intent)
        }

        // Places Autocomplete
        // Initialize Places API with your API key
        Places.initialize(applicationContext, getString(R.string.api_key))
        binding.nameTextInput.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // This block will be executed when the user touches down on the view
                startAutocompleteActivity()
            }
            true // Return true to consume the event
        }

        // Status Dropdown
        val status = resources.getStringArray(R.array.status_evacuation_center)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, status)
        binding.statusDropdown.setAdapter(arrayAdapter)

        // Save Button
        binding.saveEvacuationCenterButton.setOnClickListener {

            val nameInput = binding.nameTextInput.text.toString().trim()
            val inChargeInput = binding.inChargeTextInput.text.toString().trim()
            val contactNumInput = binding.inChargeContactNumTextInput.text.toString().trim()
            val occupantsInput = binding.occupantsTextInput.text.toString().trim()

            // Input Validation
            if (nameInput.isEmpty()) {
                binding.nameTextInput.error = "Place is required."
                binding.nameTextInput.requestFocus()
            } else if (inChargeInput.isEmpty()) {
                binding.inChargeTextInput.error = "In-Charge is required."
                binding.inChargeTextInput.requestFocus()
            } else if (contactNumInput.isEmpty()) {
                binding.inChargeContactNumTextInput.error = "Contact Number is required."
                binding.inChargeContactNumTextInput.requestFocus()
            } else if (occupantsInput.isEmpty()) {
                binding.occupantsTextInput.error = "Occupants is required."
                binding.occupantsTextInput.requestFocus()
            } else {
                data = FirebaseDatabase.getInstance()
                databaseReference = data.getReference("Evacuation Centers")
                val evacuationCenterData = EvacuationCenterData(name, address, latitude, longitude, "Available", inChargeInput, contactNumInput, occupantsInput)
                databaseReference.child(name!!).setValue(evacuationCenterData)
                Toast.makeText(this, "Evacuation Center added successfully.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EvacuationCenters::class.java)
                startActivity(intent)
            }
        }

        // Cancel Button
        binding.cancelEvacuationCenterButton.setOnClickListener {
            val intent = Intent(this, EvacuationCenters::class.java)
            startActivity(intent)
        }

    }

    private fun startAutocompleteActivity() {
        val canlubangBounds = RectangularBounds.newInstance(
            LatLng(14.2049, 121.0910), // Southwest corner of the bounding box
            LatLng(14.2110, 121.0975)  // Northeast corner of the bounding box
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        )
            .setLocationBias(canlubangBounds)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Get the user's selected place from the Intent.
                    val place = Autocomplete.getPlaceFromIntent(data!!)

                    name = place.name
                    address = place.address
                    longitude = place.latLng?.longitude.toString()
                    latitude = place.latLng?.latitude.toString()

                    binding.nameTextInput.setText(place.name)

                    Log.i(TAG, "Place: $name, $address, $longitude, $latitude")

                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    // Handle the error status
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation
                }
            }
        }
    }
}