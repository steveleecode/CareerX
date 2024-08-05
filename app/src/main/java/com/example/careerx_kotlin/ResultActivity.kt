package com.example.careerx_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var career1: TextView
    private lateinit var career2: TextView
    private lateinit var career3: TextView
    private lateinit var explain1: TextView
    private lateinit var explain2: TextView
    private lateinit var explain3: TextView

    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        career1 = findViewById(R.id.career1)
        career2 = findViewById(R.id.career2)
        career3 = findViewById(R.id.career3)

        explain1 = findViewById(R.id.explain1)
        explain2 = findViewById(R.id.explain2)
        explain3 = findViewById(R.id.explain3)

        restartButton = findViewById(R.id.restart)

        val intent = intent
        val answers = intent.getStringArrayExtra("answers")

        // Analyze answers to provide career suggestions
        if (answers != null) {
            analyzeAnswers(answers)
        }

        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun analyzeAnswers(answers: Array<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            val questions = SurveyActivity.QUESTIONS // Assuming this is accessible
            val prompt = "ChatGPT prompt: You are to assume the role of a career guidance professional for kids " +
                    "without many resources in India with unique interests and skillsets. " +
                    "For one kid, when asked " + questions.contentToString() +
                    ", they answered " + answers.contentToString() +
                    " Return a list of the 3 most relevant careers for this person along with short, engaging, and relevant descriptions for each one. " +
                    "Return a JSON with the fields \"career1\", \"career2\", \"career3\", \"explain1\", \"explain2\", \"explain3\". "

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
            val explain1_ans = jsonResult.getString("explain1")
            val explain2_ans = jsonResult.getString("explain2")
            val explain3_ans = jsonResult.getString("explain3")

            career1.text = career1_ans
            career2.text = career2_ans
            career3.text = career3_ans

            explain1.text = explain1_ans
            explain2.text = explain2_ans
            explain3.text = explain3_ans
        }
    }

    companion object {
        private const val TAG = "ResultActivity"

        fun chatGPT(prompt: String): String {
            val url = "https://api.openai.com/v1/chat/completions"
            val apiKey = "sk-proj-uKRWyN3LZcb38F3OXmAqT3BlbkFJ44UAuRGO0m8Sh67rvoc1"
            val model = "gpt-3.5-turbo"
            val temperature = 0.7

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

    }
}
