package com.example.arsnova

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.common.collect.Iterables
import com.google.common.collect.Iterables.toArray
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        populateSpinners()


    }

    fun populateSpinners() {
        val db = Firebase.firestore
        var courses = arrayListOf<String>()
        var yearLevels = arrayListOf<String>()

        db.collection("course").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                   courses.add(document.data.getValue("name").toString())

                }
            }
            .addOnCompleteListener { task ->
                courses.sort()
                val coureArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
                coureArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coureArrayAdapter.notifyDataSetChanged()

                findViewById<Spinner>(R.id.spinnerCourse).adapter = coureArrayAdapter
            }
        db.collection("year_level").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    yearLevels.add(document.data.getValue("roman_abbr").toString())

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