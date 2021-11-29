package com.example.arsnova

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text
import java.io.File

class HomepageActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    private val user = auth.currentUser
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        setSupportActionBar(findViewById(R.id.toolbarHome))

        val drawerLayout: DrawerLayout = findViewById(R.id.homepageDrawerLayout)
        val navView: NavigationView = findViewById(R.id.NavView)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.home_page, R.string.home_page)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {

                R.id.MenuHome -> Toast.makeText(this, "Home", Toast.LENGTH_LONG).show()
                R.id.MenuEditProfile -> Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show()
                R.id.MenuLogout -> Logout()
            }
            true
        }
        UpdateUi()
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

    private fun UpdateUi() {
        val header = findViewById<NavigationView>(R.id.NavView).getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.NavHeaderNameText)
        val gsRef = storage.getReferenceFromUrl("${getString(R.string.storage_profile)}/${user!!.uid}")
        val localfile = File.createTempFile("profile","")

        gsRef.getFile(localfile)
            .addOnSuccessListener() {
                header.findViewById<ImageView>(R.id.NavHeaderPicture).setImageURI(localfile.toUri())
            }
        println("${getString(R.string.storage_profile)}/${user!!.uid}")



        header.findViewById<TextView>(R.id.NavHeaderEmailText).text = user!!.email
        //header.findViewById<ImageView>(R.id.NavHeaderPicture).s

        db.collection(getString(R.string.collection_users)).document(user!!.uid).get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    val fname = result.data!!.getValue(getString(R.string.user_firstname))
                    val mi = result.data!!.getValue(getString(R.string.user_mi))
                    val lname = result.data!!.getValue(getString(R.string.user_lastname))
                    name.text = "$fname $mi. $lname"
                } else {
                    println("firestore lookup failed")
                }
            }
            .addOnFailureListener() {
                println(it)
            }

    }

}