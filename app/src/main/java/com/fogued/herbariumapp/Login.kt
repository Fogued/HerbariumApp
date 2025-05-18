package com.fogued.herbariumapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var registerNowTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.btn_login)
        progressBar = findViewById(R.id.progressBar)
        registerNowTextView = findViewById(R.id.registerNow)

        registerNowTextView.setOnClickListener {
            startActivity(Intent(this@Login, Register::class.java))
            finish()
        }

        buttonLogin.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(this@Login, "Please enter email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(this@Login, "Please enter password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressBar.visibility = ProgressBar.VISIBLE
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@Login) { task ->
                            progressBar.visibility = ProgressBar.GONE
                            if (task.isSuccessful) {
                                Toast.makeText(this@Login, "Login Success", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Login, MainActivity::class.java))
                            } else {
                                Toast.makeText(this@Login, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}