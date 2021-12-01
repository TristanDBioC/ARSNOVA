package com.example.arsnova

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text
import java.io.File

class HomepageActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    var user = auth.currentUser
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout : DrawerLayout

    private val viewModel: UserInfo by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        setSupportActionBar(findViewById(R.id.toolbarHome))
        initViewModel()
        drawerLayout = findViewById(R.id.homepageDrawerLayout)
        val navView: NavigationView = findViewById(R.id.NavView)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.home_page, R.string.home_page)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId) {
                R.id.MenuHome -> replaceFragment(HomepageFragment(), "ARS Nova")
                R.id.MenuEditProfile -> replaceFragment(EditProfileFragment(), "Edit Profile")
                R.id.MenuLogout -> Logout()
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()

        UpdateNavigationUi()
        replaceFragment(HomepageFragment(), "ARS Nova")
    }

    private fun initViewModel() {
        val gsRef = storage.getReferenceFromUrl("${getString(R.string.storage_profile)}/${user!!.uid}")
        val localFile = File.createTempFile("profile","")
        var counter = 0
        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.VISIBLE
        gsRef.getFile(localFile)
            .addOnCompleteListener() {
                viewModel.setUri(localFile.toUri())
                counter += 1
                if (counter == 2) {
                    findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
                }
            }
        db.collection(getString(R.string.collection_users)).document(user!!.uid).get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    viewModel.setDocument(result)
                } else {
                    println("firestore lookup failed")
                }
            }
            .addOnCompleteListener() {
                counter += 1
                if (counter == 2) {
                    findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
                }
            }

    }

    private fun replaceFragment(fragment : Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun Logout() {
        auth.signOut()
        intent = Intent(this, LaunchActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun UpdateNavigationUi() {
        val header = findViewById<NavigationView>(R.id.NavView).getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.NavHeaderNameText)


        val pictureUri = viewModel.getImageUri()
        if (pictureUri == null) {
            val gsRef = storage.getReferenceFromUrl("${getString(R.string.storage_profile)}/${user!!.uid}")
            val localFile = File.createTempFile("profile","")
            gsRef.getFile(localFile)
                .addOnCompleteListener() {
                    header.findViewById<ImageView>(R.id.NavHeaderPicture).setImageURI(localFile.toUri())
                }
        } else {
            header.findViewById<ImageView>(R.id.NavHeaderPicture).setImageURI(pictureUri)
        }

        val document = viewModel.getDocument()
        if (document == null) {
            header.findViewById<TextView>(R.id.NavHeaderEmailText).text = user!!.email
            db.collection(getString(R.string.collection_users)).document(user!!.uid).get()
                .addOnSuccessListener { result ->
                    if (result != null) {
                        viewModel.setDocument(result)
                        val fname = result.data!!.getValue(getString(R.string.user_firstname))
                        val mi = result.data!!.getValue(getString(R.string.user_mi))
                        val lname = result.data!!.getValue(getString(R.string.user_lastname))
                        name.text = "${fname} ${mi}. ${lname}"
                    } else {
                        println("firestore lookup failed")
                    }
                }
                .addOnFailureListener() {
                    println(it)
                }

        } else {
            val fname = document.data!!.getValue(getString(R.string.user_firstname))
            val mi = document.data!!.getValue(getString(R.string.user_mi))
            val lname = document.data!!.getValue(getString(R.string.user_lastname))
            name.text = "${fname} ${mi}. ${lname}"
        }
    }


}