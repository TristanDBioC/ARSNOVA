package com.example.arsnova

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Integer.getInteger

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        println("firebase!")
        var auth = Firebase.auth
        if (auth.currentUser != null) {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    fun buttonSigninclick(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)

    }

    fun buttonSingupclick(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

}