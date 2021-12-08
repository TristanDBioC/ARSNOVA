package com.example.arsnova

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ViewAttendanceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val auth = Firebase.auth
    private val user = auth.currentUser
    private val db = Firebase.firestore
    private val viewModel : UserInfo by activityViewModels()

    private val eventIdList = mutableListOf<String>()

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
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_attendance, container, false)


        eventIdList.clear()
        eventIdList.add("aaa")
        populateScrollview(view.findViewById(R.id.ViewAttendanceScrollView))
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
        db.collection(getString(R.string.collection_attendance))
            .whereEqualTo(getString(R.string.attendance_userID), user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.data.getValue(getString(R.string.attendance_eventId)).toString()
                    eventIdList.add(id)
                }

            }
            .addOnCompleteListener() {
                db.collection(getString(R.string.collection_events))
                    .get()
                    .addOnSuccessListener {  documents ->
                        for (document in documents) {
                            val name = document.data.getValue(getString(R.string.event_name)).toString()
                            val date = document.data.getValue(getString(R.string.event_date)).toString()
                            val start = document.data.getValue(getString(R.string.event_timeStart)).toString()
                            val end = document.data.getValue(getString(R.string.event_timeEnd)).toString()
                            val id = document.data.getValue(getString(R.string.event_qrcode)).toString()
                            val attendance = (id in eventIdList)
                            view.addView(createEventCard(name, date, start, end, attendance))
                        }
                    }
            }

    }


}