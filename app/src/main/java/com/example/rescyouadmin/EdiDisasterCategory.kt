package com.example.rescyouadmin

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.rescyouadmin.databinding.ActivityEdiDisasterCategoryBinding
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import droidninja.filepicker.FilePickerBuilder
import java.util.UUID

class EdiDisasterCategory : AppCompatActivity(), EasyPermissions.PermissionCallbacks  {

    companion object {
        const val GALLERY_PERMISSION_REQUEST_CODE = 1676
        const val CAMERA_PERMISSION_CODE = 2676

        // for camera
        const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
        const val GALLERY_IMAGE_PICK_REQUEST_CODE = 3423

        // SAVING IMAGES AND OPENING CAMERA
        var fileUri: Uri? = null
        var CAPTURE_IMAGE = 1

        const val TAG = "PinMyLocation"
    }

    private lateinit var binding: ActivityEdiDisasterCategoryBinding

    private lateinit var disasterDesc: TextView
    private lateinit var disasterTitle: TextView
    private lateinit var disasterImage: ImageView

    private lateinit var disasterImageSource: TextView
    private lateinit var disasterArticleSource: TextView
    private var key = ""
    private var imageUrl = ""

    private var isEditing: Boolean = false

    // ARRAY LIST FOR THE PHOTO
    var uri = ArrayList<Uri>()

    //for a progress dialog
    private lateinit var progressDialog: ProgressDialog

    //Storage
    private val storage = FirebaseStorage.getInstance("gs://rescyou-57570.appspot.com")

    private var tempImageUrl = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEdiDisasterCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Hide the keyboard when the activity starts
        hideKeyboard()

        //BACK BUTTON
        binding.backButton.setOnClickListener {
            val intent = Intent(this, PreparednessTips::class.java)
            startActivity(intent)
            finish()
        }

        //INITIALIZE THE VARIABLES
        disasterDesc = binding.perDisasterTips
        disasterTitle = binding.tipsTextview
        disasterImage = binding.perDisasterImage


        disasterImageSource = binding.imageSourceTextView
        disasterArticleSource = binding.articleSourceTextView

        val bundle = intent.extras
        if (bundle != null) {
            Toast.makeText(this, "bundle", Toast.LENGTH_SHORT)
            disasterDesc.text = bundle.getString("Description")
            disasterTitle.text = bundle.getString("Title")

            disasterImageSource.text = bundle.getString("Image Source")
            disasterArticleSource.text = bundle.getString("Article Source")

            key = bundle.getString("Key") ?: ""
            imageUrl = bundle.getString("Image") ?: ""
            Glide.with(this).load(bundle.getString("Image")).into(disasterImage)

            // Set the fields to disabled initially
            setEditableFields(true)

            // Hide the addPhoto_Button initially
            binding.addPhotoButton.visibility = View.VISIBLE
            binding.editButton.visibility = View.GONE
        }

        //ADD PHOTO
        binding.addPhotoButton.setOnClickListener {
            if (hasGalleryPermission()) {
                openGallery()
            } else {
                requestGalleryPermission()
            }
        }

        // Enable editing when the "Edit" button is clicked
        binding.editButton.setOnClickListener {
            showEditConfirmationDialog()
        }

        // Save the updated values when the "Save" button is clicked
        binding.saveButton.setOnClickListener {
            showSaveConfirmationDialog()
        }

        //CANCEL BUTTON
        binding.cancelButton.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showCancelConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Cancel")
        builder.setMessage("Are you sure you want to cancel?")
        builder.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(this, PreparednessTips::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }

    private fun showSaveConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Save")
        builder.setMessage("Are you sure you want to save?")
        builder.setPositiveButton("Yes") { _, _ ->
            checkInputsAndShowToast()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }

    private fun showEditConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Edit")
        builder.setMessage("Are you sure you want to edit?")
        builder.setPositiveButton("Yes") { _, _ ->
            isEditing = !isEditing
            setEditableFields(true)

            // Show the addPhoto_Button when the Edit button is clicked
            binding.addPhotoButton.visibility = View.VISIBLE
            binding.editButton.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }

    // Function to enable/disable editing of fields
    private fun setEditableFields(isEditing: Boolean) {
        disasterDesc.isEnabled = isEditing
        disasterTitle.isEnabled = isEditing
        disasterImageSource.isEnabled = isEditing
        disasterArticleSource.isEnabled = isEditing
    }

    // Function to save updated values to Firebase
    private fun saveUpdatedValues() {

        var updatedDescription = disasterDesc.text.toString()
        var updatedTitle = disasterTitle.text.toString()
        var updatedImageSource = disasterImageSource.text.toString()
        var updatedArticleSource = disasterArticleSource.text.toString()

//        val updatedData = DataClass(updatedTitle, updatedDescription, imageUrl, updatedImageSource, updatedArticleSource, key)

        // Show the progress dialog
        showLoadingDialog()


        updatedDescription = replaceNewlinesBack(updatedDescription)

        val databaseReference =
            FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        // Update the specific child with the new values
        databaseReference.child("Preparedness Tips").child(updatedTitle).child("dataDesc")
            .setValue(updatedDescription)
        databaseReference.child("Preparedness Tips").child(updatedTitle).child("dataImageSource")
            .setValue(updatedImageSource)
        databaseReference.child("Preparedness Tips").child(updatedTitle).child("dataArticleSource")
            .setValue(updatedArticleSource)
        databaseReference.child("Preparedness Tips").child(updatedTitle).child("dataImage")
            .setValue(imageUrl)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Dismiss the progress dialog after the operation completes
                    dismissLoadingDialog()
                    // Set the fields to disabled initially
                    setEditableFields(false)
                    // Hide the addPhoto_Button after updating the database
                    binding.addPhotoButton.visibility = View.GONE
                    binding.editButton.visibility = View.VISIBLE
                    binding.saveButton.visibility = View.GONE
                } else {
                    Toast.makeText(this, "Failed to update data.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to replace "\n" with "\\n" in a given string
    private fun replaceNewlinesBack(input: String): String {
        var result = input.replace("\n", "\\n")
        result = result.replace("\'", "\\'")
        return result
    }

    private fun checkInputsAndShowToast() {
        if (disasterDesc.text.toString().trim().isEmpty() ||
            disasterTitle.text.toString().trim().isEmpty() ||
            disasterImageSource.text.toString().trim().isEmpty() ||
            disasterArticleSource.text.toString().trim().isEmpty()) {

            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
        }else{
            saveUpdatedValues()
            Toast.makeText(this, "Successfully updated.", Toast.LENGTH_SHORT).show()
        }
    }

    //PROGRESS DIALOG
    private fun showLoadingDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissLoadingDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    //opening the gallery/camera
    private fun openGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, GALLERY_IMAGE_PICK_REQUEST_CODE)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, GALLERY_IMAGE_PICK_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        fileUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        //opening camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, CAPTURE_IMAGE)
    }


    //PERMISSIONS
    private fun requestCameraPermission() {
        EasyPermissions.requestPermissions(
            this,
            "Camera permission is required for taking photos via camera",
            CAMERA_PERMISSION_CODE,
            android.Manifest.permission.CAMERA
        )
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)
    }


    private fun hasGalleryPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            EasyPermissions.hasPermissions(
                this,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } else {
            EasyPermissions.hasPermissions(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun requestGalleryPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            EasyPermissions.requestPermissions(
                this,
                "Storage permission is required for accessing the gallery.",
                GALLERY_PERMISSION_REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            openGalleryForAndroid11AndAbove()

        }
    }

    private fun openGalleryForAndroid11AndAbove() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, GALLERY_IMAGE_PICK_REQUEST_CODE)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            if (requestCode == CAMERA_PERMISSION_CODE) {
                requestCameraPermission()
            } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
                requestGalleryPermission()
            }
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            openCamera()
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            openGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                uploadImageToFirebase(it)
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        //show progress dialog
        showLoadingDialog()
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("Disasters/$fileName")

        ref.putFile(fileUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    tempImageUrl = it.toString()  // Store URL in temp variable
                    Glide.with(this).load(tempImageUrl).into(disasterImage)
                    // Update the imageUrl with the temporary URL
                    imageUrl = tempImageUrl
                    dismissLoadingDialog()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }
}