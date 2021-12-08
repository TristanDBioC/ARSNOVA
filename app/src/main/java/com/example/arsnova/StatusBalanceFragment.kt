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
import androidx.lifecycle.Observer
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StatusBalanceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val auth = Firebase.auth
    private val user = auth.currentUser
    private val db = Firebase.firestore
    private val viewModel : UserInfo by activityViewModels()

    private val eventIdList = mutableListOf<String>()
    private var totalFines: Float = 0.toFloat()

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
        val view = inflater.inflate(R.layout.fragment_status_balance, container, false)
        viewModel.imageUri.observe(viewLifecycleOwner, Observer {
            view.findViewById<CircleImageView>(R.id.BalanceProfile)?.setImageURI(it)
        })
        viewModel.userDocument.observe(viewLifecycleOwner, Observer {
            val fines = it.data?.getValue(getString(R.string.user_fines)).toString().toFloat()
            val status = it.data?.getValue(getString(R.string.user_summary)).toString().toBoolean()
            val incentive = it.data?.getValue(getString(R.string.user_incentives)).toString().toFloat()
            val netBalance = fines-incentive

            view?.findViewById<TextView>(R.id.NetBalanceTextView)?.text = String.format("%s %.2f",getString(R.string.curency), netBalance)
            view?.findViewById<TextView>(R.id.TotalFinesTextView)?.text = String.format("%s %.2f",getString(R.string.curency), fines)
            view?.findViewById<TextView>(R.id.IncentivesTextView)?.text = String.format("%s %.2f",getString(R.string.curency), incentive)
            if (status) {
                view?.findViewById<TextView>(R.id.ClearanceStatusTextView)?.text = "CLEARED"
            } else {
                view?.findViewById<TextView>(R.id.ClearanceStatusTextView)?.text = "NOT CLEARED"
            }

        })
        eventIdList.clear()
        totalFines = 0.toFloat()

        populateScrollview(view.findViewById(R.id.statusBalanceScrollView))

        return view
    }

    private fun createEventCard(
        name: String,
        date: String,
        start:String,
        end: String,
        penalty: Float
    ): CardView {
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



        // TextView for Penalty Incurred
        val FinePenalty = TextView(activity)
        FinePenalty.text = ("%s %.2f".format(getString(R.string.curency), penalty))

        linearlayout.addView(eventNameTextView)
        linearlayout.addView(DateTimeTextView)
        linearlayout.addView(FinePenalty)
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
                    println(id)
                }

            }
            .addOnCompleteListener() {
                db.collection(getString(R.string.collection_events))
                    .whereNotIn(getString(R.string.event_qrcode), eventIdList)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val name = document.data.getValue(getString(R.string.event_name)).toString()
                            val date = document.data.getValue(getString(R.string.event_date)).toString()
                            val start = document.data.getValue(getString(R.string.event_timeStart)).toString()
                            val end = document.data.getValue(getString(R.string.event_timeEnd)).toString()
                            val penalty: Float = document.data.getValue(getString(R.string.event_fines)).toString().toFloat()
                            totalFines += penalty
                            view.addView(createEventCard(name, date, start, end, penalty ))
                        }
                    }
                    .addOnCompleteListener() {
                        db.collection(getString(R.string.collection_users)).document(user!!.uid)
                            .update(getString(R.string.user_fines), totalFines)
                        (activity as HomepageActivity).initViewModel()
                    }

            }

    }

    fun updateUserFines(balance: Float) {
        db.collection(getString(R.string.collection_users)).document(user!!.uid)
            .update(getString(R.string.user_fines), balance)
    }

}