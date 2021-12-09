package com.example.arsnova

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.internal.http.HttpDate.format
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.String.format
import java.util.*

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
    private val map = hashMapOf<String, Int?>()

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

    private fun createEventCard(name: String,
                                date: String,
                                start: String,
                                end: String,
                                marked: Boolean,
                                timestamp: Int?,
    ): CardView {
        // Main Card View
        val MainCard = CardView(requireActivity())
        MainCard.id = View.generateViewId()
        MainCard.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))

        val layoutparams  = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams.setMargins(0,0,0,10)
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

        // Icon for Attendance Status
        val AttendanceIcon = CircleImageView(activity)
        val AttendanceIconLayout = RelativeLayout.LayoutParams(
            100,
            100
        )
        AttendanceIconLayout.addRule(RelativeLayout.CENTER_VERTICAL)
        AttendanceIconLayout.addRule(RelativeLayout.ALIGN_PARENT_END)
        AttendanceIconLayout.setMargins(0,0,50,0)
        AttendanceIcon.layoutParams = AttendanceIconLayout


        // TextView for Attendance Status
        val AttendanceTextView = TextView(activity)
        if (marked) {
            val uri = Uri.parse("android.resource://com.example.arsnova/drawable/check")
            AttendanceTextView.text = ("Attendance is marked as present")
            AttendanceIcon.setImageURI(uri)
        } else {
            val uri = Uri.parse("android.resource://com.example.arsnova/drawable/x_mark")
            AttendanceTextView.text = ("Attendance is not marked as present")
            AttendanceIcon.setImageURI(uri)
        }

        // Relative Layout to Contain All
        val relativeLayout = RelativeLayout(activity)
        val relativelayoutparams  = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        relativelayoutparams.setMargins(0, 0,0,10)
        relativeLayout.layoutParams = relativelayoutparams


        linearlayout.addView(eventNameTextView)
        linearlayout.addView(DateTimeTextView)
        // IF there is timestamp
        if (timestamp != null) {
            // TextView for DateTime
            val timestampTextView = TextView(activity)
            timestampTextView.text = ("Attendance marked at %s".format(getDate(timestamp.toLong())))
            linearlayout.addView(timestampTextView)
        } else {
            linearlayout.addView(AttendanceTextView)
        }

        relativeLayout.addView(linearlayout)
        relativeLayout.addView(AttendanceIcon)
        MainCard.addView(relativeLayout)

        return MainCard
    }


    private fun populateScrollview(view: LinearLayout) {
        db.collection(getString(R.string.collection_attendance))
            .whereEqualTo(getString(R.string.attendance_userID), user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.data.getValue(getString(R.string.attendance_eventId)).toString()
                    val timestamp = document.data.getValue(getString(R.string.attendance_timestamp))

                    if (timestamp == null) {
                        map[id] = null
                    } else {
                        map[id] = timestamp?.toString().toInt()
                    }
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
                            val timestamp = map[id]
                            println(map)
                            val attendance = (id in eventIdList)
                            view.addView(createEventCard(name, date, start, end, attendance, timestamp))
                        }
                    }
            }

    }

    fun getDate(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("MM-dd-yyyy | HH:mm:ss",calendar).toString()
        return date
    }


}