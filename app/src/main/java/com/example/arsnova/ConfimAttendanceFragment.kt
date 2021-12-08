package com.example.arsnova

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ConfimAttendanceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val auth = Firebase.auth
    private val user = auth.currentUser
    private val db = Firebase.firestore
    private val viewModel : UserInfo by activityViewModels()


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
        val view = inflater.inflate(R.layout.fragment_confim_attendance, container, false)
        lateinit var EventID: String

        viewModel.eventDocument.observe(viewLifecycleOwner, Observer {
            val name = it.data?.getValue(getString(R.string.event_name))
            val date = it.data?.getValue(getString(R.string.event_date))
            val location = it.data?.getValue(getString(R.string.event_location))
            val start = it.data?.getValue(getString(R.string.event_timeStart))
            val end = it.data?.getValue(getString(R.string.event_timeEnd))
            val status = it.data?.getValue(getString(R.string.event_availability)).toString().toBoolean()
            EventID = it.data?.getValue(getString(R.string.event_qrcode)).toString()
            var statusText = "Attendance is Closed"
            if (status) {
                statusText = "Attendance is Open"
            }

            view.findViewById<TextView>(R.id.ConfirmEventName).text = name.toString()
            view.findViewById<TextView>(R.id.ConfirmEventDate).text = ("Event Date: %s".format(date))
            view.findViewById<TextView>(R.id.ConfirmEventLocation).text = ("Where: %s".format(location))
            view.findViewById<TextView>(R.id.ConfirmEventTimeEnd).text = ("Time Start: %s".format(start))
            view.findViewById<TextView>(R.id.ConfirmEventTimeStart).text = ("Time End: %s".format(end))
            view.findViewById<TextView>(R.id.ConfirmEventStatus).text = ("Status: %s".format(statusText))
            view.findViewById<Button>(R.id.ConfirmEventButtonConfirm).isEnabled = status
        })

        view.findViewById<TextView>(R.id.ConfirmEventButtonCancel)
            .setOnClickListener() {
                (activity as HomepageActivity).replaceFragment(ScannerFragment(), "ARS Nova")

            }

        view.findViewById<TextView>(R.id.ConfirmEventButtonConfirm)
            .setOnClickListener() {
                MarkAttendance(auth.currentUser!!.uid, EventID)
            }
        return view
    }

    private fun MarkAttendance(uid: String, eventID: String) {
        val data = hashMapOf(
            getString(R.string.attendance_eventId) to eventID,
            getString(R.string.attendance_userID) to uid,
            getString(R.string.attendance_timestamp) to System.currentTimeMillis()/1000
        )

        val attendanceRef = db.collection(getString(R.string.collection_attendance))

        db.collection(getString(R.string.collection_attendance))
            .whereEqualTo(getString(R.string.attendance_userID), uid)
            .whereEqualTo(getString(R.string.attendance_eventId), eventID)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    db.collection(getString(R.string.collection_attendance))
                        .add(data)
                        .addOnCompleteListener() {
                            Toast.makeText(requireActivity(), "Attendance Recorded", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    for (document in documents) {
                        db.collection(getString(R.string.collection_attendance)).document(document.id)
                            .update(
                                getString(R.string.attendance_timestamp), System.currentTimeMillis()/1000
                            )
                            .addOnCompleteListener() {
                                Toast.makeText(requireActivity(), "Attendance Updated", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnCompleteListener() {
                (activity as HomepageActivity)!!.replaceFragment(HomepageFragment(), "ARS Nova")
            }

    }

}