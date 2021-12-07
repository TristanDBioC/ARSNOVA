package com.example.arsnova

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.arsnova.viewmodels.UserInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomepageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomepageFragment : Fragment() {
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
        return view
    }

}