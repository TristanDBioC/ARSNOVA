package com.example.arsnova

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignupActivity : AppCompatActivity() {

    private  lateinit var auth:FirebaseAuth

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
                    findViewById<Button>(R.id.buttonRegister).isEnabled = false
                } else {
                    findViewById<Button>(R.id.buttonRegister).isEnabled = true
                }
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
                    findViewById<Button>(R.id.buttonRegister).isEnabled = false
                } else {
                    findViewById<Button>(R.id.buttonRegister).isEnabled = true
                }
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
                    findViewById<Button>(R.id.buttonRegister).isEnabled = false
                } else {
                    findViewById<Button>(R.id.buttonRegister).isEnabled = true
                }
            }

        })
    }


    fun performAuth(email: String, password: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, CreateProfileActivity::class.java)
                startActivity(intent)
                println("authentication successful")
            } else {
                println("failed")
            }

        }
    }

    fun buttonRegisteronClick(view: View) {
        val email: String = findViewById<EditText>(R.id.editTextEmail).text.toString()
        val password: String = findViewById<EditText>(R.id.editTextPassword).text.toString()

        performAuth(email, password)

    }

}
