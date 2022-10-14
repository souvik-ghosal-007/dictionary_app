package com.souvik.dictoinary

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.souvik.dictoinary.databinding.ActivityDefinitionBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class DefinitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDefinitionBinding

    private lateinit var URL: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDefinitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val word = intent.getStringExtra("word").toString()

        URL = "https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/${word}?strictMatch=false"

        val myAsyncTask = MyAsyncTask()

        myAsyncTask.execute(URL)

        binding.backIV.setOnClickListener {
            finish()
        }
    }

    inner class MyAsyncTask: AsyncTask<String, Void, Data>() {
        override fun doInBackground(vararg params: String?): Data? {
            val url = createUrl(params[0])

            var jsonResponse : String?

            try {
                jsonResponse = makeHttpResponse(url)
                val data = extractFeatureFromJson(jsonResponse)
                return data
            }catch (e: IOException) {
                Log.e("DA", "Prob")
            }
            return null
        }

        override fun onPostExecute(result: Data?) {
            super.onPostExecute(result)

            if(result == null) {
                binding.wordDefTV.text = "Check the word you entered!!"
                return
            }

            binding.wordDefTV.text = result.definition.toString()
        }
    }

    private fun extractFeatureFromJson(definitionJson: String): Data? {
        try {
            val baseJsonResponse = JSONObject(definitionJson)
            val featureResults = baseJsonResponse.getJSONArray("results")
            val firstResult = featureResults.getJSONObject(0)
            val lexicalEntries = firstResult.getJSONArray("lexicalEntries")
            val firstLexicalEntry = lexicalEntries.getJSONObject(0)
            val entries = firstLexicalEntry.getJSONArray("entries")
            val firstEntry = entries.getJSONObject(0)
            val senses = firstEntry.getJSONArray("senses")
            val firstSense = senses.getJSONObject(0)
            val definitions = firstSense.getJSONArray("definitions")

            return Data(definitions[0].toString())

        } catch (e: JSONException) {
            Log.e("DA", "Error in parsing Json")
        }

        return null
    }

    private fun makeHttpResponse(url: URL?): String {
        var jsonResponse = ""

        val urlConnection : HttpURLConnection

        var inputStream: InputStream? = null

        try {
            urlConnection = url?.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("Accept", "application/json")
            urlConnection.setRequestProperty("app_id", "1b99f28f")
            urlConnection.setRequestProperty("app_key", "6b20d6824421148c213dfa5f4cafcf8d")
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.connect()

            if(urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromInputStream(inputStream)
            }
            else {
                Log.d("Definition Activity", "Error Response Code: ${urlConnection.responseCode}")
            }

            urlConnection.disconnect()
            inputStream?.close()
        } catch (exception: IOException) {
            Log.e("Definition Activity", "Connection Error")
        }

        return jsonResponse
    }

    private fun readFromInputStream(inputStream: InputStream?): String {
        val output = StringBuilder()

        if(inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()

            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }

        return output.toString()
    }

    private fun createUrl(stringUrl: String?): URL? {
        var url: URL? = null

        try {
            url = URL(stringUrl)
        } catch (exception: MalformedURLException) {
            Log.d("Definition Activity", "Error in creating url")
        }

        return url
    }
}