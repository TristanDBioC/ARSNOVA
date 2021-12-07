package com.example.arsnova

import android.content.pm.PackageManager
import android.os.Bundle
import android.security.ConfirmationAlreadyPresentingException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.*
import com.example.arsnova.viewmodels.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScannerFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var codeScanner: CodeScanner
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
        // Inflate the layout for this fragment
        (activity as HomepageActivity)!!.setupPermissions()
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        val scanner_view = view.findViewById<CodeScannerView>(R.id.CodeScanner_View)

        codeScanner = CodeScanner(requireActivity(), scanner_view)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }
        codeScanner.decodeCallback = DecodeCallback {
            attendanceProcess(it.text)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val permission = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            codeScanner.startPreview()
        } else {
            Toast.makeText(requireActivity(), "Exit app to request permission again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    private fun attendanceProcess(qrString: String) {
        println("attendance Process")
        db.collection(getString(R.string.collection_events)).document(qrString).get()
            .addOnSuccessListener() {
                if (it != null) {
                    viewModel.setEvent(it)
                    (activity as HomepageActivity)!!.replaceFragment(ConfimAttendanceFragment(), "Confirm Attendance")
                }
            }
    }
}