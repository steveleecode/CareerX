package com.example.careerx_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.careerx_kotlin.databinding.ActivitySignupBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    lateinit var username : EditText
    lateinit var password: EditText
    lateinit var phone: EditText
    lateinit var name: EditText
    lateinit var signupButton: Button
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = binding.signupUsername
        password = binding.signupPassword
        signupButton = binding.signupButton
        phone = binding.signupPhone
        name = binding.signupName

        signupButton.setOnClickListener(View.OnClickListener {
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("users")

            val profile = Profile(username.text.toString(), hash(password.text.toString()), name.text.toString(), phone.text.toString())
            reference.child(username.text.toString()).setValue(profile)

            if (username.text.toString() != "" && password.text.toString() != "" && name.text.toString() != "" && phone.text.toString() != "" ){
                Toast.makeText(this, "You have signed up sucessfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Signup Failed! Please make sure all fields are completed.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hash(string: String): String {
        val bytes = string.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}