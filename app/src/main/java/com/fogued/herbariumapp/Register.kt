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

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var loginNowTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val buttonReg = findViewById<Button>(R.id.btn_register)
        progressBar = findViewById(R.id.progressBar)
        loginNowTextView = findViewById(R.id.loginNow)

        loginNowTextView.setOnClickListener {
            startActivity(Intent(this@Register, Login::class.java))
            finish()
        }

        buttonReg.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(this@Register, "Please enter email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(this@Register, "Please enter password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressBar.visibility = ProgressBar.VISIBLE
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@Register) { task ->
                            progressBar.visibility = ProgressBar.GONE
                            if (task.isSuccessful) {
                                Toast.makeText(this@Register, "Register Success", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Register, MainActivity::class.java))
                            } else {
                                Toast.makeText(this@Register, "Register Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}