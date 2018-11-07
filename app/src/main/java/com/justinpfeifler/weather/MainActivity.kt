package com.justinpfeifler.weather

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private var weatherArrayAdapter: WeatherArrayAdapter? = null
    private var weatherListView: ListView? = null
    private var weatherList = ArrayList<Weather>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        weatherListView = findViewById<View>(R.id.weatherListView) as ListView
        weatherArrayAdapter = WeatherArrayAdapter(this, weatherList)
        weatherListView!!.adapter = weatherArrayAdapter

        fab.setOnClickListener {view ->
            val locationEditText = findViewById<View>(R.id.locationEditText) as EditText
            val url = createURL(locationEditText.text.toString())

            if (url != null) {
                dismissKeyboard(locationEditText)
                val getLocationWeatherTask = GetWeatherTask()
                getLocationWeatherTask.execute(url)
            } else {
                Snackbar.make(
                    findViewById(R.id.coordinator),
                    R.string.invalid_url,
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun createURL(city: String) : URL? {
        val key = getString(R.string.api_key)
        val url = getString(R.string.web_service_url)

        try {
            val urlString = url + URLEncoder.encode(city, "UTF-8") + "&units=imperial&cnt=16&APPID=" + key
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private inner class GetWeatherTask : AsyncTask<URL, Void, JSONObject>() {
        override fun doInBackground(vararg params: URL): JSONObject? {
            var connection: HttpURLConnection? = null

            try {
                connection = params[0].openConnection() as HttpURLConnection
                val response = connection.responseCode

                if (response == HttpURLConnection.HTTP_OK) {
                    val builder = StringBuilder()

                    try {
                        BufferedReader(InputStreamReader(connection.inputStream)).use {
                            reader ->
                            var line: String
                            line = reader.readLine()
                            builder.append(line)
                            while (line.isNotEmpty()) {
                                builder.append(line)
                                line = reader.readLine()
                            }
                        }
                    } catch (e: Exception) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error, Snackbar.LENGTH_LONG).show()
                        e.printStackTrace()
                    }

                    return JSONObject(builder.toString())
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(weather: JSONObject?) {
            weather?.let { convertJSONToList(it) }
            weatherArrayAdapter!!.notifyDataSetChanged()
            weatherListView!!.smoothScrollToPosition(0)
        }
    }

    private fun convertJSONToList(forecast: JSONObject) {
        var list = forecast.getJSONArray("list")
        for (index in 0 until list.length()) {
            val day = list.getJSONObject(index)
            val temperatures = day.getJSONObject("temp")
            val weather = day.getJSONArray("weather").getJSONObject(0)
            weatherList.add( Weather(
                day.getLong("dt"),
                temperatures.getDouble("min"),
                temperatures.getDouble("max"),
                day.getDouble("humidity"),
                weather.getString("description"),
                weather.getString("icon")
            ))
        }
    }

    private fun dismissKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
