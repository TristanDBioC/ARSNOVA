package com.example.arsnova

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

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