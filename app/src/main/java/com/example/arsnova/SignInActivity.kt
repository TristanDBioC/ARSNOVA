package com.example.arsnova

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class SignInActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        auth = Firebase.auth
        findViewById<Button>(R.id.buttonSignin).isEnabled = false

        findViewById<EditText>(R.id.editTextEmail)
            .addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(findViewById<EditText>(R.id.editTextEmail).text.toString()).matches()) {
                        findViewById<EditText>(R.id.editTextEmail).error = "Not a valid email"
                        findViewById<Button>(R.id.buttonSignin).isEnabled = false
                    } else {
                        findViewById<Button>(R.id.buttonSignin).isEnabled = true
                    }
                }

            })

    }

    fun signInButtonOnClick(view: View) {
        findViewById<Button>(R.id.buttonSignin).isEnabled = false
        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.VISIBLE
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        lateinit var intent : Intent

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    val db = Firebase.firestore
                    db.collection(getString(R.string.collection_users)).document(user.uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                Toast.makeText(this, "Proceed to homepage", Toast.LENGTH_SHORT).show()
                            } else {
                                intent = Intent(this, CreateProfileActivity::class.java)
                            }
                            findViewById<Button>(R.id.buttonSignin).isEnabled = true
                            startActivity(intent)
                        }
                } else {
                    Toast.makeText(this, "Login authentication failed", Toast.LENGTH_SHORT).show()
                }
                findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
            }
    }

    fun forgetPasswordCLick(view: View) {
        val intent = Intent(this, Resetpassword::class.java)
        startActivity(intent)
    }

}