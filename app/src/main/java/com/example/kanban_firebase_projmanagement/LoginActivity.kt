package com.example.kanban_firebase_projmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var  emailEditText: EditText
    private lateinit var  passwordEditText: EditText
    private lateinit var  loginButton: Button
    private lateinit var  registerLink: TextView


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initialise firebase auth
        auth = FirebaseAuth.getInstance()
        //initialise views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)

        //set login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
               loginUser(email, password)
            }
        }
        //set register link click listener
        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loginUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    //handle successful login
                    //navigate to KanbanActivity on successful login
                    //Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, KanbanActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    //handle login failure
                    Toast.makeText(this, "Authentication failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

            }
    }
}