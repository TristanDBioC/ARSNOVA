package com.example.arsnova

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.common.collect.Iterables
import com.google.common.collect.Iterables.toArray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateProfileActivity : AppCompatActivity() {
    private lateinit var user : FirebaseUser
    private lateinit var auth : FirebaseAuth
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            findViewById<ImageView>(R.id.imageProfilePic).setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        auth = Firebase.auth
        user = auth.currentUser!!

        populateSpinners()
        findViewById<EditText>(R.id.editTextemail).setText(user.email)

        val profilePic = findViewById<ImageView>(R.id.imageProfilePic)
        profilePic.setOnClickListener() {
            getContent.launch("image/*")
        }


    }

    private fun populateSpinners() {
        val db = Firebase.firestore
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

}