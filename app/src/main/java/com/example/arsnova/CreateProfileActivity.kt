package com.example.arsnova

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CreateProfileActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    private val user = auth.currentUser!!
    private val db = Firebase.firestore
    private var imageProfileURI: Uri? = null
    private var FirestoreUploadIsFinished = false
    private var CloudUploadIsFinished = false

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            findViewById<ImageView>(R.id.imageProfilePic).setImageURI(uri)
            imageProfileURI = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)


        populateSpinners()
        findViewById<EditText>(R.id.editTextProfileEmail).setText(user.email)

        val profilePic = findViewById<ImageView>(R.id.imageProfilePic)
        profilePic.setOnClickListener() {
            getContent.launch("image/*")
        }
    }

    private fun populateSpinners() {
        var courses = arrayListOf<String>()
        var yearLevels = arrayListOf<String>()

        db.collection(getString(R.string.collection_courses)).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                   courses.add(document.data.getValue(getString(R.string.course_name)).toString())

                }
            }
            .addOnCompleteListener { task ->
                courses.sort()
                val coureArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
                coureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coureArrayAdapter.notifyDataSetChanged()

                findViewById<Spinner>(R.id.spinnerCourse).adapter = coureArrayAdapter
                findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
            }
        db.collection(getString(R.string.collection_yearLevel)).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    yearLevels.add(document.data.getValue(getString(R.string.yearLevel_abbreviation)).toString())

                }
            }
            .addOnCompleteListener { task ->
                yearLevels.sort()
                val yearLevelsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearLevels)
                yearLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                yearLevelsAdapter.notifyDataSetChanged()

                findViewById<Spinner>(R.id.spinnerYearLevel).adapter = yearLevelsAdapter
            }

    }

    fun buttonFinished(view: View) {
        if (!checkField()) {
            triggerLoading()
            addFirestoreUser()
            uploadImageToCloud(imageProfileURI)
        }

    }

    private fun triggerLoading() {
        findViewById<Button>(R.id.buttonFinishedSetup).isEnabled = false
        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.VISIBLE
    }

    private fun finishLoading() {
        if (FirestoreUploadIsFinished and CloudUploadIsFinished) {
            findViewById<Button>(R.id.buttonFinishedSetup).isEnabled = true
            findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
        }
    }


    private fun addFirestoreUser() {
        val newUser = hashMapOf(
            getString(R.string.user_firstname) to findViewById<EditText>(R.id.editTextFirstName).text.toString(),
            getString(R.string.user_lastname) to findViewById<EditText>(R.id.editTextLastName).text.toString(),
            getString(R.string.user_mi) to findViewById<EditText>(R.id.editTextMI).text.toString(),
            getString(R.string.user_email) to findViewById<EditText>(R.id.editTextProfileEmail).text.toString(),
            getString(R.string.user_dept) to findViewById<Spinner>(R.id.spinnerCourse).selectedItem.toString(),
            getString(R.string.user_yearLevel) to findViewById<Spinner>(R.id.spinnerYearLevel).selectedItem.toString(),
            getString(R.string.user_bio) to findViewById<EditText>(R.id.editTextBio).text.toString(),
            getString(R.string.user_fines) to 0.0,
            getString(R.string.user_incentives) to 0.0,
            getString(R.string.user_type) to "regular",
            getString(R.string.user_position) to ""
        )
        db.collection(getString(R.string.collection_users)).document(user.uid).set(newUser)
            .addOnSuccessListener { Toast.makeText(this, "Profile successfully created", Toast.LENGTH_LONG).show() }
            .addOnFailureListener {Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener {
                FirestoreUploadIsFinished = true
                finishLoading()
            }

    }

    private fun checkField() : Boolean{
        var thereIsError = false;
        val firstName = findViewById<EditText>(R.id.editTextFirstName)
        val lastName = findViewById<EditText>(R.id.editTextLastName)
        val MI = findViewById<EditText>(R.id.editTextMI)
        val bio = findViewById<EditText>(R.id.editTextBio)

        if (firstName.text.length == 0) {
            firstName.error = "Must not be empty"
            thereIsError = true
        }

        if (lastName.text.length == 0) {
            lastName.error = "Must nt be empty"
            thereIsError = true
        }

        return thereIsError
    }

    private fun uploadImageToCloud(uri: Uri?) {
        if (uri != null) {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val fileref = storageRef.child("images/${user.uid}")
            fileref.putFile(uri)
                .addOnFailureListener {
                    Toast.makeText(this, "file not uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this, "file uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    CloudUploadIsFinished = true
                    finishLoading()
                }
        }

    }

}