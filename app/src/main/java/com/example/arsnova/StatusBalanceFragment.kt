package com.example.arsnova

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.arsnova.viewmodels.UserInfo
import de.hdodenhof.circleimageview.CircleImageView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StatusBalanceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
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
            if (status) {
                view?.findViewById<TextView>(R.id.ClearanceStatusTextView)?.text = "CLEARED"
            } else {
                view?.findViewById<TextView>(R.id.ClearanceStatusTextView)?.text = "NOT CLEARED"
            }

        })

        return view
    }

}