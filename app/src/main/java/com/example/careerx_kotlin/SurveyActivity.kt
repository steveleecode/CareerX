package com.example.careerx_kotlin

import SurveyAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerx_kotlin.R
import com.example.careerx_kotlin.ResultActivity

data class SurveyOption(val title: String, val description: String, val imageResId: Int)

class SurveyActivity : AppCompatActivity() {

    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var nextButton: Button
    private lateinit var questionCount: TextView
    private lateinit var questionText: TextView

    private var currentQuestionIndex = 0
    private lateinit var answers: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        // Setup the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "CareerX"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        questionRecyclerView = findViewById(R.id.question_recycler_view)
        nextButton = findViewById(R.id.next_button)
        questionCount = findViewById(R.id.qnum)
        questionText = findViewById(R.id.question_text)

        answers = Array(QUESTIONS.size) { "" }

        setupRecyclerView()
        displayQuestion()

        nextButton.setOnClickListener {
            if (answers[currentQuestionIndex].isNotEmpty()) {
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

    private fun setupRecyclerView() {
        questionRecyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun displayQuestion() {
        questionCount.text = "Question ${currentQuestionIndex + 1} of ${QUESTIONS.size}"
        val currentQuestion = QUESTIONS[currentQuestionIndex]
        questionText.text = currentQuestion

        val options = OPTIONS[currentQuestionIndex].mapIndexed { index, optionText ->
            //SurveyOption(optionText, "Description for $optionText", IMAGES[currentQuestionIndex][index])
            SurveyOption(optionText, DESCRIPTIONS[currentQuestionIndex][index], IMAGES[currentQuestionIndex][index])
        }

        val adapter = SurveyAdapter(options) { selectedOption: SurveyOption ->
            answers[currentQuestionIndex] = selectedOption.title
        }
        questionRecyclerView.adapter = adapter
    }

    companion object {
        val QUESTIONS = arrayOf(
            "Which of these activities would you participate in on your free time?",
            "Which of these 4 would you most enjoy learning?",
            "What type of film would you most like to watch?",
            "What brings you the most peace?",
            "Which of the following are you best at?",
            "What subject are you best at?",
            "What elective are you best at?",
            "Which of these skills are you best at?"
        )

        private val OPTIONS = arrayOf(
            arrayOf("Legos", "Sports", "Learn", "Draw"),
            arrayOf("Cars", "Politics", "Digital Media", "Fashion"),
            arrayOf("Superheroes", "Wildlife", "Detectives", "Cooking"),
            arrayOf("Nature", "Music", "Socializing", "Cleaning"),
            arrayOf("Running", "Playing an instrument", "Arts and Crafts", "Writing"),
            arrayOf("Math", "History", "Science", "English"),
            arrayOf("Woodworking", "Physical Education", "Drama", "Culture"),
            arrayOf("Fast Thinking",  "Time Management", "Teamwork", "Creativity" )
        )

        private val DESCRIPTIONS = arrayOf(
            arrayOf("Spend time building your favorite Lego set or making a 3D model", "Go outside and play some football or other sports with friends", "Sit down and learn about a new subject or topic", "Get a pen and paper and sketch whatever you can imagine"),
            arrayOf("Learn about different cars and how they work", "Learn about how countries are run and how people gain power", "Learn about the arts of photography and how your favorite videos were created", "Learn about the clothes you wear and how they were made"),
            arrayOf("Watch an action filled movie with superheroes defeating villains with their powers", "Watch a documentary on how your favorite animal survives in the wild", "Watch a movie about a detective solving a tough mystery", "Watch a cooking or baking show with top chefs making different cuisines"),
            arrayOf("Take a walk and look at the world outside while admiring plants and animals", "Sit in your room with headphones on, listening to your favorite music", "Hang out with friends and meet new people", "Tidy up your room or house to make it neat and comfortable for you"),
            arrayOf("Enjoying the thrill of running", "Whether it be with a guitar or drum, you always make the best tunes", "Create something beautiful with the materials around you", "Poems or books, you can make a story out of basic words"),
            arrayOf("Numbers and equations have always made sense to you", "The events of the past have always felt very interesting to you", "You enjoy the nature of scientific findings and inquiry", "You have always loved writing essays or reading books"),
            arrayOf("Using tools to turn wood into art has nothing difficult for you", "Running, playing, and exercising is your favorite part of school", "Acting is fun and you enjoy acting in front of crowds", "You are heavily interested in culture, its history, and traditions that people participate in"),
            arrayOf("Make quick decisions based on your situation at any given time", "Finish your work on time and never be late", "Always work well with others and be productive in a group", "Have a lot of new ideas to work with")
        )

        private val IMAGES = arrayOf(
            arrayOf(R.drawable.lego_image, R.drawable.sports_image, R.drawable.learn_image, R.drawable.draw_image), // Images for question 1
            arrayOf(R.drawable.cars_image, R.drawable.politics_image, R.drawable.digital_media_image, R.drawable.fashion_image), // Images for question 2
            arrayOf(R.drawable.superheroes_image, R.drawable.wildlife_image, R.drawable.detectives_image, R.drawable.cooking_image), // Images for question 3
            arrayOf(R.drawable.nature_image, R.drawable.music_image, R.drawable.socializing_image, R.drawable.cleaning_image), // Images for question 4
            arrayOf(R.drawable.running_image, R.drawable.playing_instrument_image, R.drawable.arts_crafts_image, R.drawable.writing_image), // Images for question 5
            arrayOf(R.drawable.math_image, R.drawable.history_image, R.drawable.science_image, R.drawable.english_image), // Images for question 6
            arrayOf(R.drawable.woodworking_image, R.drawable.physical_education_image, R.drawable.drama_image, R.drawable.culture_image), // Images for question 7
            arrayOf(R.drawable.fast, R.drawable.time, R.drawable.team, R.drawable.create)
        )
    }
}
