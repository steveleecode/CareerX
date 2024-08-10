package com.example.careerx_kotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageButton
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val imageview = findViewById<ImageView>(R.id.imageView)
        imageview.setImageResource(R.drawable.smile)

        val startSurveyButton: Button = findViewById(R.id.start_survey_button)
        startSurveyButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SurveyActivity::class.java)
            startActivity(intent)
        }

        val loginButton: ImageButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val resultsButton: Button = findViewById(R.id.results)
        if (isSurveyCompleted(this@MainActivity)) {
            resultsButton.visibility = View.VISIBLE
            resultsButton.setOnClickListener {
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                intent.putExtra("past_results", true)
                startActivity(intent)
            }
        } else {
            resultsButton.visibility = View.GONE
        }
    }

    fun isSurveyCompleted(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SurveyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("SurveyCompleted", false)
    }

}



