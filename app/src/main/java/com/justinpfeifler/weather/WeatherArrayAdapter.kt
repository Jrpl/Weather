package com.justinpfeifler.weather

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL

internal class WeatherArrayAdapter
(
    context: Context,
    forecast: List<Weather>
) : ArrayAdapter<Weather>(context, -1, forecast) {

    // use this to store already downloaded bitmaps for reuse later
    private val bitmaps = HashMap<String, Bitmap>()

    // class for reusing views as items scroll in and out of view
    private class ViewHolder {
        internal var conditionImageView: ImageView? = null
        internal var dayTextView: TextView? = null
        internal var lowTextView: TextView? = null
        internal var highTextView: TextView? = null
        internal var humidityTextView: TextView? = null
    }

    // create the custom view for the ListViews
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View {
        var convertView = convertView
        // get the weather object at this position
        var day = getItem(position)

        val viewHolder: ViewHolder

        // check for reusable holder, if one's not available create a new one
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflator = LayoutInflater.from(context)
            convertView = inflator.inflate(R.layout.list_item, parent, false)
            viewHolder.conditionImageView = convertView!!.findViewById(R.id.conditionImageView) as ImageView
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView) as TextView
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView) as TextView
            viewHolder.highTextView = convertView.findViewById(R.id.highTextView) as TextView
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // if the weather icon is downloaded use it, otherwise download it
        if (bitmaps.containsKey(day!!.iconURL)) {
            viewHolder.conditionImageView!!.setImageBitmap(bitmaps[day.iconURL])
        } else {
            // download image
        }

        // populate views with weather data
        viewHolder.dayTextView!!.text = context.getString(R.string.day_description, day.dayOfWeek, day.description)
        viewHolder.lowTextView!!.text = context.getString(R.string.low_temp, day.minTemp)
        viewHolder.humidityTextView!!.text = context.getString(R.string.humidity, day.humidity)

        return convertView
    }

    // load weather condition icons in seperate thread
    private inner class LoadImage (private val imageView: ImageView?) : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg params: String): Bitmap? {
            var bitmap: Bitmap? = null
            var connection: HttpURLConnection? = null

            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection

                try {
                    connection.inputStream.use {
                        inputStream ->
                        bitmap = BitmapFactory.decodeStream(inputStream)
                        bitmaps.put(params[0], bitmap!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(bitmap: Bitmap) {
            imageView?.setImageBitmap(bitmap)
        }
    }
}