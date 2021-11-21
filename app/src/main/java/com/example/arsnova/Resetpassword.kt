package com.example.arsnova

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Resetpassword : AppCompatActivity() {
    private val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        val email = findViewById<EditText>(R.id.editTextEmaillPReset)
        email.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    email.error = "Not a valid email"
                    findViewById<Button>(R.id.buttonResetPass).isEnabled = false
                } else {
                    findViewById<Button>(R.id.buttonResetPass).isEnabled = true
                }
            }
        })
    }

    fun sendResetPassEmail(view: View) {
        val email = findViewById<EditText>(R.id.editTextEmaillPReset).text.toString()

        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.VISIBLE
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email verification sent", Toast.LENGTH_SHORT).show()
                    this.finish()
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
            }
    }
}