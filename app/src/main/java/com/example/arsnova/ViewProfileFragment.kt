package com.example.arsnova

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.arsnova.R
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel : UserInfo by activityViewModels()
    private val auth = Firebase.auth

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
        val view = inflater.inflate(R.layout.fragment_view_profile, container, false)
        viewModel.imageUri.observe(viewLifecycleOwner, Observer {
            view.findViewById<CircleImageView>(R.id.profileProfilePic)?.setImageURI(it)
        })
        viewModel.userDocument.observe(viewLifecycleOwner, Observer {
            val fname = it.data?.getValue(getString(R.string.user_firstname))
            val mi = it.data?.getValue(getString(R.string.user_mi))
            val lname = it.data?.getValue(getString(R.string.user_lastname))
            val level = it.data?.getValue(getString(R.string.user_yearLevel))
            val course = it.data?.getValue(getString(R.string.user_dept))

            view.findViewById<TextView>(R.id.profileFullname)?.text = "$fname $mi. $lname"
            view.findViewById<TextView>(R.id.profileEmail)?.text = it.data?.getValue(getString(R.string.user_email)).toString()
            view.findViewById<TextView>(R.id.profileBio)?.text = it.data?.getValue(getString(R.string.user_bio)).toString()
            view.findViewById<TextView>(R.id.profileLevelCourse)?.text = "$level - $course"

        })

        view.findViewById<TextView>(R.id.ClickableLogout)
            .setOnClickListener() {
                auth.signOut()
                val intent = Intent(activity, LaunchActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        view.findViewById<Button>(R.id.profileButtonEditProfile)
            .setOnClickListener() {
                (activity as HomepageActivity)!!.replaceFragment(EditProfileFragment(), "Balance and Status")
            }

        view.findViewById<LinearLayout>(R.id.StatusBalanceHyperlink)
            .setOnClickListener() {
                (activity as HomepageActivity)!!.replaceFragment(StatusBalanceFragment(), "Balance and Status")
            }
        return view
    }


}