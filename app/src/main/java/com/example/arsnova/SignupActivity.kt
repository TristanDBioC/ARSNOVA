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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignupActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private var emailFormat : Boolean = false
    private var passwordFormat : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val email: EditText = findViewById(R.id.editTextEmail)
        val password: EditText = findViewById(R.id.editTextPassword)
        val passwordConfirm: EditText = findViewById(R.id.editTextConfPass)

        email.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    email.error = "Not a valid email"
                    emailFormat = false
                } else {
                    emailFormat = true
                }
                registerButtonClickableCheck()
            }

        })
        passwordConfirm.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (password.text.toString() != passwordConfirm.text.toString()) {
                    passwordConfirm.error = "Password does not match"
                    passwordFormat = false
                } else {
                    passwordFormat = true
                }
                registerButtonClickableCheck()
            }
        })
        password.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (password.text.toString() != passwordConfirm.text.toString()) {
                    passwordConfirm.error = "Password does not match"
                    passwordFormat = false
                } else {
                    passwordConfirm.error = null
                    passwordFormat = true
                }
                registerButtonClickableCheck()
            }
        })
    }

    fun registerButtonClickableCheck() {
        println(emailFormat)
        println(passwordFormat)
        val passwordtextbox = findViewById<EditText>(R.id.editTextPassword)
        if ((passwordFormat) and (passwordtextbox.text.length < resources.getInteger(R.integer.passowrdlength))) {
            passwordFormat = false
            findViewById<EditText>(R.id.editTextConfPass).error = "Password must be at least 8 characters"
        } else {
            passwordFormat = (passwordFormat) and (passwordtextbox.text.length >= resources.getInteger(R.integer.passowrdlength))
        }
        findViewById<Button>(R.id.buttonRegister).isEnabled = emailFormat and passwordFormat
    }

    fun performAuth(email: String, password: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
                val intent = Intent(this, CreateProfileActivity::class.java)
                startActivity(intent)
            } else {
                findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
                Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
            }
            findViewById<Button>(R.id.buttonSignup).isEnabled = true

        }
    }

    fun buttonRegisteronClick(view: View) {
        val email: String = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password: String = findViewById<EditText>(R.id.editTextPassword).text.toString()
        findViewById<Button>(R.id.buttonSignup).isEnabled = false
        findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.VISIBLE
        performAuth(email, password)

    }
}
