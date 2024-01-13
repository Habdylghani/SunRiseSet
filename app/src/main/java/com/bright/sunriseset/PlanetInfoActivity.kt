package com.bright.sunriseset

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.bright.sunriseset.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId


import android.content.res.Configuration
import android.text.format.DateFormat

import java.util.Locale

class PlanetInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var languageSpinner: Spinner
    private var sunriseTime: LocalDateTime? = null
    private var sunsetTime: LocalDateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the current time
        val currentTime = LocalDateTime.now()

        // Asynchronously fetch sunrise and sunset times
        GlobalScope.launch(Dispatchers.Main) {
            val sunriseDeferred = async(Dispatchers.IO) { fetchTime("sunrise") }
            val sunsetDeferred = async(Dispatchers.IO) { fetchTime("sunset") }

            // Await the results of asynchronous tasks
            sunriseTime = sunriseDeferred.await()
            sunsetTime = sunsetDeferred.await()

            // If both sunrise and sunset times are available, localize and display them
            if (sunriseTime != null && sunsetTime != null) {
                // Update UI with localized times
                updateUIText()
            }
        }

        // Set up language selection using Spinner
        setupLanguageSpinner()
    }

    private fun setupLanguageSpinner() {
        languageSpinner = findViewById(R.id.languageSpinner)
        val languageOptions = resources.getStringArray(R.array.language_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Update language based on the selected item
                updateLanguage(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
            }
        }
    }

    private fun updateLanguage(selectedPosition: Int) {
        try {
            val newLocale = when (selectedPosition) {
                0 -> Locale.getDefault()
                1 -> Locale.FRENCH
                2 -> Locale.CHINESE
                else -> Locale.getDefault()
            }

            // Update the app's configuration with the new locale
            val configuration = Configuration(resources.configuration)
            configuration.setLocale(newLocale)
            resources.updateConfiguration(configuration, resources.displayMetrics)

            // Manually update UI components with new language
            updateUIText()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUIText() {
        val localizedSunrise = getLocalizedTime(sunriseTime as LocalDateTime, this@PlanetInfoActivity)
        val localizedSunset = getLocalizedTime(sunsetTime as LocalDateTime, this@PlanetInfoActivity)

        binding.textViewSunrise.text = "${getString(R.string.SunriseTime)} $localizedSunrise"
        binding.textViewSunset.text = "${getString(R.string.SunsetTime)} $localizedSunset"
    }

    /**
     * Retrieves a localized time string based on the user's preferred language.
     *
     * @param time The LocalDateTime to be formatted.
     * @param context The application context to access resources and preferences.
     * @return A string representation of the localized time.
     */
    private fun getLocalizedTime(time: LocalDateTime, context: Context): String {
        // Retrieve the user's preferred language from the device settings
        val userPreferredLanguage = Locale.getDefault().language

        // Added it to determine if the language should use 24-hour format
        val is24HourFormat = DateFormat.is24HourFormat(context)

        // Create a SimpleDateFormat with the user's preferred language and 12/24-hour format used in the fetchTime but it wasn't working as I expected
        val pattern = if (is24HourFormat) "HH:mm" else "hh:mm a"
        val sdf = SimpleDateFormat(pattern, Locale(userPreferredLanguage))

        // Format the LocalDateTime into a string using the specified SimpleDateFormat
        return sdf.format(
            time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }


    // Coroutine function to fetch sunrise or sunset time from the Sunrise-Sunset API
    private suspend fun fetchTime(type: String): LocalDateTime? {
        return try {
            val apiUrl =
                URL("https://api.sunrise-sunset.org/json?lat=37.7749&lng=-122.4194&formatted=0")
            val urlConnection: HttpURLConnection = apiUrl.openConnection() as HttpURLConnection
            try {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val jsonResponse = JSONObject(response.toString())
                val timeUTC = jsonResponse.getJSONObject("results").getString(type)
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
                val dateTime = formatter.parse(timeUTC)
                LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault())
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

