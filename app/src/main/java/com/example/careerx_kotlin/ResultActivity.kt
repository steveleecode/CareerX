package com.example.careerx_kotlin

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {

    private lateinit var career1: TextView
    private lateinit var career2: TextView
    private lateinit var career3: TextView
    private lateinit var explain1: TextView
    private lateinit var explain2: TextView
    private lateinit var explain3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        career1 = findViewById(R.id.career1)
        career2 = findViewById(R.id.career2)
        career3 = findViewById(R.id.career3)

        explain1 = findViewById(R.id.explain1)
        explain2 = findViewById(R.id.explain2)
        explain3 = findViewById(R.id.explain3)

        val intent = intent
        val answers = intent.getStringArrayExtra("answers")

        // Analyze answers to provide career suggestions
        if (answers != null) {
            analyzeAnswers(answers)
        }
    }

    private fun analyzeAnswers(answers: Array<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            val questions = SurveyActivity.QUESTIONS // Assuming this is accessible
            val prompt = "ChatGPT prompt: You are " +
                    "to assume the role of a career guidance professional for kids " +
                    "without many resources in India with unique interests and skillsets. " +
                    "For one kid, when asked ${Arrays.toString(questions)}" +
                    ", they answered ${Arrays.toString(answers)}" +
                    "Return a list of the 3 most relevant careers for this person, with no other output at all." +
                    "This is the format to follow, and do not place any spaces before or after the commas and " +
                    "make sure that the short explainations are formatted to be stand-alone from eachother " +
                    "(no putting them in a list): " +
                    "\"CAREER1,CAREER2,CAREER3|SHORTEXPLAINATIONOFCAREER1,SHORTEXPLAINATIONOFCAREER2,SHORTEXPLAINATIONOFCAREER3\""

            val result = withContext(Dispatchers.IO) {
                chatGPT(prompt)
            }
            val output = result.split("|")
            val careers = output[0].split(",")
            val explainations = output[1].split(",")

            career1.text = careers[0]
            career2.text = careers[1]
            career3.text = careers[2]

            explain1.text = explainations[0]
            explain2.text = explainations[1]
            explain3.text = explainations[2]
        }
    }

    companion object {
        private const val TAG = "ResultActivity"

        fun chatGPT(prompt: String): String {
            val url = "https://api.openai.com/v1/chat/completions"
            val apiKey = "sk-proj-NZQ0uEEjh0II61SAYlAaT3BlbkFJ5CwOoPh5Yc0P17wDM998"
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
            val end = response.indexOf("\"", start)
            return response.substring(start, end)
        }
    }
}
