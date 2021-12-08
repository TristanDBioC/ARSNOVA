package com.example.arsnova

import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.print.PrintAttributes
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.marginBottom
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.arsnova.viewmodels.UserInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomepageFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel : UserInfo by activityViewModels()
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        viewModel.imageUri.observe(viewLifecycleOwner, Observer {
            view.findViewById<CircleImageView>(R.id.homepagePicture)?.setImageURI(it)
        })
        viewModel.userDocument.observe(viewLifecycleOwner, Observer {
            val fname = it.data?.getValue(getString(R.string.user_firstname))
            val mi = it.data?.getValue(getString(R.string.user_mi))
            val lname = it.data?.getValue(getString(R.string.user_lastname))
            view.findViewById<TextView>(R.id.homepageHiFname)?.text = "Hi ${it.data?.getValue(getString(R.string.user_firstname)).toString()}!"
            view.findViewById<TextView>(R.id.homepageFullname)?.text = "$fname $mi. $lname"
            view.findViewById<TextView>(R.id.homepageEmail)?.text = it.data?.getValue(getString(R.string.user_email)).toString()
        })
        view.findViewById<FloatingActionButton>(R.id.ButtonCodeScanner)
            .setOnClickListener() {
                (activity as HomepageActivity)!!.replaceFragment(ScannerFragment(), "Scan Attendance QR")
            }

        populateScrollview(view.findViewById(R.id.homepageScrollView))
        return view
    }

    private fun createEventCard(name: String, date: String, start: String, end: String, marked: Boolean): CardView {
        // Main Card View
        val MainCard = CardView(requireActivity())
        MainCard.id = View.generateViewId()
        MainCard.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))

        val layoutparams  = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams.setMargins(0, 0, 0, 10)
        MainCard.layoutParams = layoutparams

        // Linear Layout to contain elements
        val linearlayout = LinearLayout(activity)
        linearlayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )

        linearlayout.orientation = LinearLayout.VERTICAL
        linearlayout.setPadding(30, 10, 10, 10)

        // TextView for EventName
        val eventNameTextView = TextView(activity)
        eventNameTextView.text = name
        eventNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.toFloat())
        eventNameTextView.setTypeface(Typeface.DEFAULT_BOLD)

        // TextView for DateTime
        val DateTimeTextView = TextView(activity)
        DateTimeTextView.text = ("%s | %s to %s".format(date, start, end))


        // TextView for Attendance Status
        val AttendanceTextView = TextView(activity)
        if (marked) {
            AttendanceTextView.text = ("Attendance is marked as present")
        } else {
            AttendanceTextView.text = ("Attendance is not marked as present")
        }

        linearlayout.addView(eventNameTextView)
        linearlayout.addView(DateTimeTextView)
        linearlayout.addView(AttendanceTextView)
        MainCard.addView(linearlayout)
        return MainCard
    }

    private fun populateScrollview(view: LinearLayout) {
        db.collection(getString(R.string.collection_events))
            .whereEqualTo(getString(R.string.event_availability), true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.data.getValue(getString(R.string.event_name)).toString()
                    val date = document.data.getValue(getString(R.string.event_date)).toString()
                    val start = document.data.getValue(getString(R.string.event_timeStart)).toString()
                    val end = document.data.getValue(getString(R.string.event_timeEnd)).toString()
                    val eventId = document.data.getValue(getString(R.string.event_qrcode)).toString()
                    val marked = checkIfPresent(auth.currentUser!!.uid, eventId)
                    println(name)
                    view.addView(createEventCard(name, date, start, end, marked))
                }
            }
    }

    private fun checkIfPresent(uid: String, eventID: String): Boolean {
        var returnValue = false
        db.collection(getString(R.string.collection_attendance))
            .whereEqualTo(getString(R.string.attendance_userID), uid)
            .whereEqualTo(getString(R.string.attendance_eventId), eventID)
            .get()
            .addOnSuccessListener {
                returnValue = !it.isEmpty
            }

        return returnValue
    }
}