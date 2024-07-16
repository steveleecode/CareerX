package com.example.careerx_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startSurveyButton: Button = findViewById(R.id.start_survey_button)
        startSurveyButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SurveyActivity::class.java)
            startActivity(intent)
        }
    }
}
