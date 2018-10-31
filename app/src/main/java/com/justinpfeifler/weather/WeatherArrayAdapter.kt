package com.justinpfeifler.weather

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

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
    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup) : View {
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
    })
}