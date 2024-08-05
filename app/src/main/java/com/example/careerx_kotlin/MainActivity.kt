package com.example.careerx_kotlin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar);
        getSupportActionBar()?.setTitle("CareerX");

        val imageview = findViewById<ImageView>(R.id.imageView)
        imageview.setImageResource(R.drawable.smile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val startSurveyButton: Button = findViewById(R.id.start_survey_button)
        startSurveyButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SurveyActivity::class.java)
            startActivity(intent)
        }
    }
}


