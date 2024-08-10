package com.example.careerx_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.careerx_kotlin.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var loginUsername : EditText
    lateinit var loginPassword: EditText
    lateinit var loginButton: Button
    lateinit var signupRedirectText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginUsername = binding.loginUsername
        loginPassword = binding.loginPassword
        loginButton = binding.loginButton
        signupRedirectText = binding.signupRedirectText

        loginButton.setOnClickListener(View.OnClickListener {
            if(!validateUsername() || !validatePassword()) {
                Toast.makeText(this, "Login Failed! Please check your username and password.", Toast.LENGTH_SHORT).show()
            } else {
                checkUser()
            }
        })

        signupRedirectText.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                this@LoginActivity,
                SignupActivity::class.java
            )
            startActivity(intent)
        })



    }

    private fun validateUsername(): Boolean {
        val username = loginUsername.text.toString()
        if (username.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            return false
        } else {
            loginUsername.error = null
            return true
        }
    }

    private fun validatePassword(): Boolean {
        val password = loginPassword.text.toString()
        if(password.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            return false
        } else {
            loginPassword.error = null
            return true
        }
    }

    private fun hash(string: String): String {
        val bytes = string.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    fun checkUser() {
        val userUsername = loginUsername.text.toString().trim()
        val userPassword = loginPassword.text.toString().trim()
        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    loginUsername.error = null
                    val passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java)
                    if (hash(passwordFromDB.toString()) == userPassword) {
                        loginUsername.error = null
                        val nameFromDB = snapshot.child(userUsername).child("name").getValue(String::class.java)
                        val emailFromDB = snapshot.child(userUsername).child("email").getValue(String::class.java)
                        val usernameFromDB = snapshot.child(userUsername).child("username").getValue(String::class.java)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("name", nameFromDB)
                        intent.putExtra("email", emailFromDB)
                        intent.putExtra("username", usernameFromDB)
                        intent.putExtra("password", passwordFromDB)
                        startActivity(intent)
                    } else {
                        loginPassword.error = "Invalid Credentials"
                        loginPassword.requestFocus()
                    }
                } else {
                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

}