package com.justinpfeifler.weather

import android.content.Context
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
                getLocalWeatherTask.execute(url)
            } else {
                Snackbar.make(
                    findViewById(R.id.coordinator),
                    R.string.invalid_url,
                    Snackbar.LENGTH_LONG).show()
            }
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
