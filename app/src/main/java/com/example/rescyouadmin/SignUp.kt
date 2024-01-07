package com.example.rescyouadmin

import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rescyouadmin.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.regex.Pattern

class SignUp : AppCompatActivity(){

    private lateinit var binding: ActivitySignUpBinding

    //Initialize variables
//Personal Information
    private lateinit var firstName: String
    private lateinit var middleName: String
    private lateinit var lastName: String
    private lateinit var displayName: String
    private lateinit var suffixName: String
    private lateinit var birthday: String
    private var age: Int = 0

    //Account Information
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var bqrt_id: String
    val bqrtList = ArrayList<String>()


    //For Realtime Database
    private lateinit var database: DatabaseReference
    private lateinit var data: FirebaseDatabase

    //for FirebaseAuth (SIGN UP/REGISTRATION OF USER)
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    private lateinit var userID: String


    //Pssword matcher
    val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$"
    val passwordMatcher = Regex(passwordPattern)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize Firebase Auth
        auth = Firebase.auth

        // For Sign Up
        binding.signUpButton.setOnClickListener {
            //Get the values from TextInputEditText


            //ACCOUNT INFORMATION
            email = binding.emailTextInput.text.toString()
            password = binding.passwordTextInput.text.toString()
            bqrt_id= binding.bqrtTextInput.text.toString()


//            Check for the required values
            if (bqrt_id.trim().length == 0 || email.trim().length == 0) {
                Toast.makeText(
                    applicationContext,
                    "Please fill up the requred field/s.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (checkEmail(email) == false) {
                Toast.makeText(applicationContext, "Invalid Email", Toast.LENGTH_SHORT).show()

            }else if (!passwordMatcher.matches(password.trim())) {
                AlertDialog.Builder(this)
                    .setMessage("Password should be at least 6 characters, with at least 1 uppercase, 1 lowercase letter and a special character.")
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            } else if (checkEmail(email) == false && password.trim().length < 6) {
                Toast.makeText(
                    applicationContext,
                    "Invalid Email and password should be at least 6 characters.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                //Get the list of bqrt_id from the database
                getBQRTList()





            }
        }

    }

    private fun getBQRTList() {
        val database = FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("BQRT")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange called")

                for (postSnapshot in dataSnapshot.children) {
                    val bqrt = postSnapshot.child("BQRT_ID").getValue(Long::class.java)
                    bqrtList.add(bqrt.toString())
                }

                // Call checkIfBQRTExists() after the bqrtList has been populated
                checkIfBQRTExists()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
    }

    private fun checkIfBQRTExists() {
        if (bqrtList.contains(bqrt_id)) {
            Log.d(TAG, "bqrt_id exists in the list")
            signUpUser()

        } else {
            Toast.makeText(applicationContext, "Sorry, the BQRT ID does not exists.", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "bqrt_id does not exist in the list")
        }
    }

    private fun signUpUser() {

        showTermsAndConditions()

    }

    private fun showTermsAndConditions() {
        val dialog = Dialog(this@SignUp)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_terms_and_conditions)

        // Find the TextView in the dialog
        val termsTextView = dialog.findViewById<TextView>(R.id.TC_content)
        var termsAndConditions = getString(R.string.termsAncConditions_content)
        termsAndConditions = termsAndConditions.replace("\n", "<br/>").replace(" ", "&nbsp;")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            termsTextView.text = Html.fromHtml(termsAndConditions, Html.FROM_HTML_MODE_COMPACT)
        } else {
            termsTextView.text = Html.fromHtml(termsAndConditions)
        }

        if (!isFinishing) {
            dialog.show()
        }
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.rgb(241, 242, 242)))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)




        //AGREE BUTTON
        val agreeButton = dialog.findViewById<Button>(R.id.agreeButton)
        agreeButton.setOnClickListener {
            val checkBox = dialog.findViewById<CheckBox>(R.id.acceptCheckbox)
            if (checkBox.isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Do you accept the terms and conditions?")
                    .setPositiveButton("Yes") { _, _ ->






                        //SIGN UP THE USER
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success")
                                    // SIGN UP
                                    // Sign in success, update UI with the signed-in user's information

                                    val user = auth.currentUser
                                    userID= user?.uid.toString()

                                    updateUI(user)
                                    storeData(
                                        userID,
                                        bqrt_id,
                                        email,
                                    )
                                    val intent = Intent(this, Home::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    dialog.dismiss()
                                }else
                                {
                                    Toast.makeText(
                                        baseContext, "Sign Up failed. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Toast.makeText(this, "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show()
            }
        }

        //DISAGREE BUTTON
        val declineButton = dialog.findViewById<Button>(R.id.declineButton)
        declineButton.setOnClickListener {
            AlertDialog.Builder(this@SignUp)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to decline?")
                .setPositiveButton("Yes") { _, _ ->
                    // Navigate to Home activity if the user clicks "No"
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    //Email Address Pattern
    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    //Check Email Pattern
    private fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }




    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    fun storeData(
        userID: String,
        bqrtID: String,
        email: String,
    ) {
        //initialize database
        database = Firebase.database.reference

        data =
            FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = data.reference

        //store data to the REALTIME DATABASE
        myRef.child("Admins").child(userID).child("email").setValue(email)
        myRef.child("Admins").child(userID).child("bqrt_id").setValue(bqrtID)


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(ContentValues.TAG, token)

            // Save the FCM token to Firebase
            saveFcmTokenToFirebase(token)
        })


    }

    private fun saveFcmTokenToFirebase(token: String?) {

        val database =
            FirebaseDatabase.getInstance("https://rescyou-57570-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = database.reference

        // Save the FCM token to the "fcmToken" field in the user's node
        userRef.child("Admins").child(userID).child("fcmToken").setValue(token)

    }
}