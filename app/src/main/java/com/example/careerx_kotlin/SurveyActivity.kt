package com.example.careerx_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SurveyActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var answerGroup: RadioGroup
    private lateinit var nextButton: Button

    private var currentQuestionIndex = 0
    private lateinit var answers: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        questionText = findViewById(R.id.question_text)
        answerGroup = findViewById(R.id.answer_group)
        nextButton = findViewById(R.id.next_button)

        answers = Array(QUESTIONS.size) { "" }

        displayQuestion()

        nextButton.setOnClickListener {
            val selectedOptionId = answerGroup.checkedRadioButtonId
            if (selectedOptionId != -1) {
                val selectedOption = findViewById<RadioButton>(selectedOptionId)
                answers[currentQuestionIndex] = selectedOption.text.toString()

                if (currentQuestionIndex < QUESTIONS.size - 1) {
                    currentQuestionIndex++
                    displayQuestion()
                } else {
                    // All questions answered, navigate to result activity
                    val intent = Intent(this@SurveyActivity, ResultActivity::class.java)
                    intent.putExtra("answers", answers)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this@SurveyActivity, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayQuestion() {
        questionText.text = QUESTIONS[currentQuestionIndex]

        answerGroup.removeAllViews()
        for (option in OPTIONS[currentQuestionIndex]) {
            val radioButton = RadioButton(this)
            radioButton.text = option
            answerGroup.addView(radioButton)
        }
        answerGroup.clearCheck()
    }

    companion object {
        val QUESTIONS = arrayOf(
            "Which of these activities would you participate in on your free time?",
            "Which of these 4 would you most enjoy learning?",
            "What type of film would you most like to watch?",
            "What brings you the most peace?",
            "Which of the following are you best at?",
            "What subject are you best at?",
            "What elective are you best at?"
        )

        private val OPTIONS = arrayOf(
            arrayOf("Legos", "Sports", "Learn", "Draw"),
            arrayOf("Cars", "Politics", "Digital Media", "Fashion"),
            arrayOf("Superheroes", "Wildlife", "Detectives", "Cooking"),
            arrayOf("Nature", "Music", "Socializing", "Cleaning"),
            arrayOf("Running", "Playing an instrument", "Arts and Crafts", "Writing"),
            arrayOf("Math", "History", "Science", "English"),
            arrayOf("Woodworking", "Physical Education", "Drama", "Culture")
        )
    }
}
