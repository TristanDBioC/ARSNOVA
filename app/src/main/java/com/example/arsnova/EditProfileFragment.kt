package com.example.arsnova

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val auth = Firebase.auth
    private val user = auth.currentUser!!
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
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        lateinit var imageUri: Uri
        viewModel.imageUri.observe(viewLifecycleOwner, Observer {
            view.findViewById<CircleImageView>(R.id.EditProfilePicture)?.setImageURI(it)
            imageUri = it
        })
        viewModel.userDocument.observe(viewLifecycleOwner, Observer {
            val fname = it.data?.getValue(getString(R.string.user_firstname))
            val mi = it.data?.getValue(getString(R.string.user_mi))
            val lname = it.data?.getValue(getString(R.string.user_lastname))
            val level = it.data?.getValue(getString(R.string.user_yearLevel))
            val course = it.data?.getValue(getString(R.string.user_dept))

            view.findViewById<TextView>(R.id.EditProfileFullName)?.text = "$fname $mi. $lname"
            view.findViewById<EditText>(R.id.EditProfileEditTextBio)?.setText(it.data?.getValue(getString(R.string.user_bio)).toString())
            view.findViewById<TextView>(R.id.EditProfileYearCourse)?.text = "$level - $course"

        })

        view.findViewById<Button>(R.id.EditProfileButtonSave)
            .setOnClickListener() {
                val biotext: String = view.findViewById<EditText>(R.id.EditProfileEditTextBio)?.text.toString()
                updateProfile(biotext)
            }

        return view
    }


    private fun updateProfile(bio: String) {
        db.collection(getString(R.string.collection_users)).document(user.uid)
            .update(getString(R.string.user_bio), bio)
                .addOnFailureListener {
                    Toast.makeText(activity, "profile update failed", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Toast.makeText(activity, "profile updated", Toast.LENGTH_SHORT).show()
                    (activity as HomepageActivity).initViewModel()
                }

    }


}