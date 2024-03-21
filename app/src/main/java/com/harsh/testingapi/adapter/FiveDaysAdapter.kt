package com.harsh.testingapi.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harsh.testingapi.DataClasses.ForecastRecyclerFiveDays
import com.harsh.testingapi.R
import java.util.Date
import java.util.TimeZone
import java.text.SimpleDateFormat


class FiveDaysAdapter(private val forecastDataList: List<ForecastRecyclerFiveDays>) : RecyclerView.Adapter<FiveDaysAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.five_days_recycler_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastDataList[position]
        holder.bind(forecast)

    }

    override fun getItemCount(): Int {
        return forecastDataList.size
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        private val phraseTextView: TextView = itemView.findViewById(R.id.tv_phrase)
        private val highTempTextView: TextView = itemView.findViewById(R.id.tv_high_temp)
        private val lowTempTextView: TextView = itemView.findViewById(R.id.tv_low_temp)
        private val windSpeedTextView: TextView = itemView.findViewById(R.id.tv_windspeed)
        private val uvTextView: TextView = itemView.findViewById(R.id.tv_rv_uv_index_fivedays)
        private val sunriseTextView: TextView = itemView.findViewById(R.id.tv_rv_sunrise_fivedays)
        private val sunsetTextView: TextView = itemView.findViewById(R.id.tv_rv_sunset_fivedays)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iv_icon)
        private val recyclerInfo : LinearLayout = itemView.findViewById(R.id.ll_recycler_info)
        private val recyclerTop : LinearLayout = itemView.findViewById(R.id.lltop_recycler_item)


        fun bind(forecast: ForecastRecyclerFiveDays) {
            dateTextView.text = forecast.date.convertDateFormat2()
            phraseTextView.text = forecast.dayPhrase
            highTempTextView.text = "${forecast.maxTemperature}°"
            lowTempTextView.text = "${forecast.minTemperature}°"
            windSpeedTextView.text = "${forecast.windSpeed} km/h"
            uvTextView.text = "${forecast.uvIndexValue}, ${forecast.uvIndexCategory}"
            sunriseTextView.text = forecast.sunrise.convertDateFormat()
            sunsetTextView.text = forecast.sunset.convertDateFormat()
            setWeatherIcon(forecast.dayIcon)
            recyclerTop.setOnClickListener(){
                recyclerInfo.visibility = if (recyclerInfo.visibility == View.VISIBLE) View.GONE else View.VISIBLE

            }
            // Set your icon here based on forecast data
            // iconImageView.setImageResource(R.drawable.your_icon)
        }
        private fun setWeatherIcon(iconDataReceived: Int) {
            // Check if the iconDataReceived is within valid range and not in the excluded list
            if (iconDataReceived in 1..44 && iconDataReceived !in listOf(9, 10, 19, 27, 28)) {
                val resourceId = when (iconDataReceived) {
                    1 -> R.drawable.ic_1
                    2 -> R.drawable.ic_2
                    3 -> R.drawable.ic_3
                    4 -> R.drawable.ic_4
                    5 -> R.drawable.ic_5
                    6 -> R.drawable.ic_6
                    7 -> R.drawable.ic_7
                    8 -> R.drawable.ic_8
                    11 -> R.drawable.ic_11
                    12 -> R.drawable.ic_12
                    13 -> R.drawable.ic_13
                    14 -> R.drawable.ic_14
                    15 -> R.drawable.ic_15
                    16 -> R.drawable.ic_16
                    17 -> R.drawable.ic_17
                    18 -> R.drawable.ic_18
                    20 -> R.drawable.ic_20
                    21 -> R.drawable.ic_21
                    22 -> R.drawable.ic_22
                    23 -> R.drawable.ic_23
                    24 -> R.drawable.ic_24
                    25 -> R.drawable.ic_25
                    26 -> R.drawable.ic_26
                    29 -> R.drawable.ic_29
                    30 -> R.drawable.ic_30
                    31 -> R.drawable.ic_31
                    32 -> R.drawable.ic_32
                    33 -> R.drawable.ic_33
                    34 -> R.drawable.ic_34
                    35 -> R.drawable.ic_35
                    36 -> R.drawable.ic_36
                    37 -> R.drawable.ic_37
                    38 -> R.drawable.ic_38
                    39 -> R.drawable.ic_39
                    40 -> R.drawable.ic_40
                    41 -> R.drawable.ic_41
                    42 -> R.drawable.ic_42
                    43 -> R.drawable.ic_43
                    44 -> R.drawable.ic_44
                    else -> R.drawable.sunny // Default icon resource ID
                }
                iconImageView.setImageResource(resourceId)
            } else {
                // Handle invalid icon data or excluded values
                iconImageView.setImageResource(R.drawable.sunny)
            }
        }


        fun String.convertDateFormat(): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            val outputFormat = SimpleDateFormat("HH:mm")
            val date = inputFormat.parse(this) ?: Date()
            outputFormat.timeZone = TimeZone.getDefault()
            return outputFormat.format(date)
        }

        fun String.convertDateFormat2(): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            val outputFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = inputFormat.parse(this) ?: Date()
            outputFormat.timeZone = TimeZone.getDefault()
            return outputFormat.format(date)
        }
    }


    }

