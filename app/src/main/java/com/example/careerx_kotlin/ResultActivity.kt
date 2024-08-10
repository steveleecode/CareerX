package com.example.careerx_kotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

data class Career(val title: String, val description: String)

class ResultActivity : AppCompatActivity() {

    private lateinit var career1: TextView
    private lateinit var career2: TextView
    private lateinit var career3: TextView
    private lateinit var explain1: TextView
    private lateinit var explain2: TextView
    private lateinit var explain3: TextView

    private var currentlyOpenedLayout: LinearLayout? = null

    private lateinit var results: List<String>

    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        val surveyResults: Boolean = intent.getBooleanExtra("past_results", false)
        if (!surveyResults) {
            clearSurveyResults()
        }
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_result)
        setContentView(R.layout.activity_loading)

        val savedCareers = loadCareers()

        if(savedCareers != null){
            populateUI(savedCareers)
        } else {
            // Retrieve the answers from the intent
            val intent1 = intent
            val answers = intent1.getStringArrayExtra("answers")

            // Analyze answers to provide career suggestions
            if (answers != null) {
                analyzeAnswers(answers) { result ->
                    results = result;
                }

            }

        }
    }

    private fun analyzeAnswers(answers: Array<String>, callback: (List<String>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val questions = SurveyActivity.QUESTIONS // Assuming this is accessible
            val prompt = "ChatGPT prompt: You are to assume the role of a career guidance professional for kids " +
                    "without many resources in India with unique interests and skillsets. " +
                    "For one kid, when asked " + questions.contentToString() +
                    ", they answered " + answers.contentToString() +
                    " Return a list of the 5 most relevant careers for this person along with short, " +
                    "engaging, and relevant descriptions for each as well as an average salary range. " +
                    "Return a JSON with the fields \"career1\", \"career2\", \"career3\", \"career4\", " +
                    "\"career5\",\"explain1\", \"explain2\", \"explain3\", \"explain4\", \"explain5\", " +
                    "\"salary1\", \"salary2\", \"salary3\", \"salary4\", and \"salary5\"."

            val result = withContext(Dispatchers.IO) {
                chatGPT(prompt)
            }.trim().replace("\\n", "")
            val cleanedResult = result.replace("\\", "")
            Log.d(TAG, "Output: $cleanedResult")
            val jsonResult = JSONObject(cleanedResult)

            // Extract the values from the JSON object
            val career1_ans = jsonResult.getString("career1")
            val career2_ans = jsonResult.getString("career2")
            val career3_ans = jsonResult.getString("career3")
            val career4_ans = jsonResult.getString("career4")
            val career5_ans = jsonResult.getString("career5")
            val explain1_ans = jsonResult.getString("explain1") + "\n\nExpected Salary Range: " + jsonResult.getString("salary1") + " USD"
            val explain2_ans = jsonResult.getString("explain2") + "\n\nExpected Salary Range: " + jsonResult.getString("salary2") + " USD"
            val explain3_ans = jsonResult.getString("explain3") + "\n\nExpected Salary Range: " + jsonResult.getString("salary3") + " USD"
            val explain4_ans = jsonResult.getString("explain4") + "\n\nExpected Salary Range: " + jsonResult.getString("salary4") + " USD"
            val explain5_ans = jsonResult.getString("explain5") + "\n\nExpected Salary Range: " + jsonResult.getString("salary5") + " USD"

            val careers = listOf(
                Career(career1_ans, explain1_ans),
                Career(career2_ans, explain2_ans),
                Career(career3_ans, explain3_ans),
                Career(career4_ans, explain4_ans),
                Career(career5_ans, explain5_ans)
            )

            saveCareers(careers)
            saveSurveyCompletionStatus(this@ResultActivity)

            callback(listOf(career1_ans, career2_ans, career3_ans, explain1_ans, explain2_ans, explain3_ans))

            // Update the UI with the results
            setContentView(R.layout.activity_result)
            val container = findViewById<LinearLayout>(R.id.container)

            careers.forEach { career ->
                val itemView =
                    LayoutInflater.from(this@ResultActivity).inflate(R.layout.accordion_item, container, false)
                val titleView = itemView.findViewById<TextView>(R.id.careerTitle)
                val expandableLayout = itemView.findViewById<LinearLayout>(R.id.expandableLayout)
                val descriptionView = itemView.findViewById<TextView>(R.id.careerDescription)

                titleView.text = career.title
                descriptionView.text = career.description

                setupAccordion(titleView, expandableLayout, descriptionView, this@ResultActivity)
                container.addView(itemView)
            }


            restartButton = findViewById(R.id.restart)
            restartButton.setOnClickListener {
                val intent2 = Intent(this@ResultActivity, MainActivity::class.java)
                startActivity(intent2)
            }


        }
    }

    private fun saveCareers(careers: List<Career>) {
        val sharedPreferences = getSharedPreferences("career_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(careers)

        editor.putString("careers_list", json)
        editor.apply()
    }

    private fun loadCareers(): List<Career>? {
        val sharedPreferences = getSharedPreferences("career_data", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("careers_list", null)

        return if (json != null) {
            val type = object : TypeToken<List<Career>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun saveSurveyCompletionStatus(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("SurveyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("SurveyCompleted", true)
        editor.apply()
    }

    private fun populateUI(careers: List<Career>) {
        setContentView(R.layout.activity_result)
        val container = findViewById<LinearLayout>(R.id.container)

        careers.forEach { career ->
            val itemView =
                LayoutInflater.from(this@ResultActivity).inflate(R.layout.accordion_item, container, false)
            val titleView = itemView.findViewById<TextView>(R.id.careerTitle)
            val expandableLayout = itemView.findViewById<LinearLayout>(R.id.expandableLayout)
            val descriptionView = itemView.findViewById<TextView>(R.id.careerDescription)

            titleView.text = career.title
            descriptionView.text = career.description

            setupAccordion(titleView, expandableLayout, descriptionView, this@ResultActivity)
            container.addView(itemView)
        }

        restartButton = findViewById(R.id.restart)
        restartButton.setOnClickListener {
            val intent2 = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(intent2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearSurveyResults()
    }

    private fun clearSurveyResults() {
        // Clear the survey completion status
        val surveyPrefs = getSharedPreferences("SurveyPrefs", Context.MODE_PRIVATE)
        val surveyEditor = surveyPrefs.edit()
        surveyEditor.clear()
        surveyEditor.apply()

        // Clear the saved career results
        val careerPrefs = getSharedPreferences("career_data", Context.MODE_PRIVATE)
        val careerEditor = careerPrefs.edit()
        careerEditor.clear()
        careerEditor.apply()
    }



    companion object {
        private const val TAG = "ResultActivity"

        fun chatGPT(prompt: String): String {
            val url = "https://api.openai.com/v1/chat/completions"
            val apiKey = "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"
            val model = "gpt-3.5-turbo"
            val temperature = 0.3

            return try {
                val obj = URL(url)
                val connection = obj.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $apiKey")
                connection.setRequestProperty("Content-Type", "application/json")

                // The request body
                val messages = listOf(mapOf("role" to "user", "content" to prompt))
                val requestBody = mapOf(
                    "model" to model,
                    "messages" to messages,
                    "temperature" to temperature
                )

                val jsonBody = Gson().toJson(requestBody)
                Log.d(TAG, "Request Body: $jsonBody")

                connection.doOutput = true
                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonBody)
                writer.flush()
                writer.close()

                // Check for HTTP error response code
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Error in response: $responseCode - ${connection.responseMessage}")
                    return "Error: $responseCode - ${connection.responseMessage}"
                }

                // Response from ChatGPT
                val br = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (br.readLine().also { line = it } != null) {
                    response.append(line)
                }
                br.close()

                // Calls the method to extract the message.
                extractMessageFromJSONResponse(response.toString())

            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "IOException: ${e.message}")
                "Error: ${e.message}"
            }
        }
        fun extractMessageFromJSONResponse(response: String): String {
            val start = response.indexOf("content") + 11
            return response.substring(start)
        }
        private fun setupAccordion(
            titleView: TextView,
            expandableLayout: LinearLayout,
            descriptionView: TextView,
            activity: ResultActivity
        ) {
            titleView.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    // Close the currently opened layout if it exists and is not the same as the one being clicked
                    activity.currentlyOpenedLayout?.let {
                        if (it != expandableLayout) {
                            TransitionManager.beginDelayedTransition(it.parent as ViewGroup, AutoTransition())
                            it.visibility = View.GONE
                        }
                    }

                    // Open the clicked layout
                    TransitionManager.beginDelayedTransition(titleView.parent as ViewGroup, AutoTransition())
                    expandableLayout.visibility = View.VISIBLE
                    activity.currentlyOpenedLayout = expandableLayout

                } else {
                    TransitionManager.beginDelayedTransition(titleView.parent as ViewGroup, AutoTransition())
                    expandableLayout.visibility = View.GONE
                    activity.currentlyOpenedLayout = null
                }
            }
        }

    }
}
