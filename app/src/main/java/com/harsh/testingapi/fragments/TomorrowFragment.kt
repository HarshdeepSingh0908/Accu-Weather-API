package com.harsh.testingapi.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.harsh.testingapi.DataClasses.DailyForecastResponse
import com.harsh.testingapi.DataClasses.ForecastRecyclerFiveDays
import com.harsh.testingapi.R
import com.harsh.testingapi.WeatherApiService
import com.harsh.testingapi.adapter.FiveDaysAdapter
import com.harsh.testingapi.databinding.FragmentTomorrowBinding
import com.harsh.testingapi.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TomorrowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TomorrowFragment : Fragment() {
    private val BASE_URL = "https://dataservice.accuweather.com/"
    private val API_KEY = "ElQQjE7fc7McpPRkEWFIcI3IK8jXHvxG"
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var dailyForecastResponse: DailyForecastResponse
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var binding : FragmentTomorrowBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.locationKey.observe(viewLifecycleOwner) { key ->
            // Use the locationKey here
            Log.e("Tomorrow key:",key)

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            weatherApiService = retrofit.create(WeatherApiService::class.java)
            fetchWeatherDataUsingLocationKey(key)

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            weatherApiService = retrofit.create(WeatherApiService::class.java)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTomorrowBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.locationKey.observe(viewLifecycleOwner) { key ->
            // Use the locationKey here
            Log.e("Tomorrow key:",key)

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            weatherApiService = retrofit.create(WeatherApiService::class.java)
            fetchWeatherDataUsingLocationKey(key)

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TomorrowFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TomorrowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherDataUsingLocationKey(locationKey: String) {
        Log.e("key in to",locationKey)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (!locationKey.isNullOrBlank()) {
                    val forecastResponse = weatherApiService.get5DayDailyForecast(
                        locationKey.toInt().toString(),
                        API_KEY,
                        true,
                        "en-us",
                        true
                    )
                    if (forecastResponse.isSuccessful) {
                        dailyForecastResponse = forecastResponse.body()!!
                        Log.e("Data Response:", forecastResponse.toString())

                        val tomorrow = LocalDate.now().plusDays(1)

                        for (forecast in dailyForecastResponse.dailyForecasts) {
                            val forecastDate = LocalDate.parse(forecast.date.split("T")[0])

                            // Check if the forecast date matches tomorrow's date
                            if (forecastDate == tomorrow) {
                                Log.e("Forecast for Tomorrow:", forecast.toString())

                                // Extract relevant data and store it in variables
                                val maxTemperature = forecast.temperature.maximum.value.toFloat()
                                val minTemperature = forecast.temperature.minimum.value.toFloat()
                                val dayHigh = maxTemperature
                                val dayLow = minTemperature
                                val sunrise = forecast.sun.rise
                                val sunset = forecast.sun.set
                                val uvIndexValue = forecast.airAndPollen.firstOrNull { it.name == "UVIndex" }?.value.toString()
                                val uvIndexCategory = forecast.airAndPollen.firstOrNull { it.name == "UVIndex" }?.category.toString()
                                val dayIcon = forecast.day.icon
                                val dayPhrase = forecast.day.iconPhrase
                                val windSpeed = forecast.day.wind.speed.value.toString()

                                // Store the forecast data in variables for later use
                                val sunriseTime = LocalTime.parse(sunrise.substring(11, 16)) // Extract time portion and parse sunrise time
                                val sunsetTime = LocalTime.parse(sunset.substring(11, 16)) // Extract time portion and parse sunset time

                                val currentTime = LocalTime.now()
                                val isDayTime = currentTime.isAfter(sunriseTime) && currentTime.isBefore(sunsetTime)

                                if (isDayTime) {
                                    // Set data for day
                                    // For example:
                                    val iconDay = forecast.day.icon
                                    setIcon(iconDay)
                                    val humidityMax = forecast.day.relativeHumidity.maximum.toString()
                                    val headline = forecast.day.iconPhrase
                                    val convertedSunrise = getTimeFromDateString(sunrise)
                                    val convertedSunset = getTimeFromDateString(sunset)

                                    // Set data on views
                                    binding.tvHighestTemp.text = "High $dayHigh° ↑"
                                    binding.tvLowestTemp.text = "Low $dayLow° ↓"
                                    binding.tvSunriseTime.text = "Sunrise:\n$convertedSunrise"
                                    binding.tvSunsetTime.text = "Sunset:\n$convertedSunset"
                                    // Set other views accordingly
                                } else {
                                    // Set data for night
                                    // For example:
                                    val nightIcon = forecast.night.icon
                                    // Set other night data accordingly
                                }

                                // Since we found tomorrow's forecast, we can break out of the loop
                                break
                            }
                        }
                    }
                    else {
                        Log.e("WeatherFetch", "Failed to fetch weather data: ${forecastResponse.message()}")
                        Toast.makeText(requireContext(), "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Location key not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("WeatherFetch", "Exception: ${e.message}")
                Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun fahrenheitToCelsius(fahrenheit: Float): Float {
        return (fahrenheit - 32) * 5 / 9
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToCurrentLocalTime(sunTime: String): String {
        val utcTime = LocalDateTime.parse(sunTime.substring(0, 19))
        val zoneId = ZoneId.of("UTC")
        val zonedDateTime = ZonedDateTime.of(utcTime, zoneId)
        val currentZone = ZoneId.systemDefault()
        val convertedTime = zonedDateTime.withZoneSameInstant(currentZone)
        val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
        return convertedTime.format(formatter)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeFromDateString(timestamp: String): String {
        val dateTime = OffsetDateTime.parse(timestamp)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return dateTime.format(formatter)
    }
    private fun setIcon(nightIcon: Int?) {
        for (nightIcon in 1..44) {

            if (nightIcon == 1){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_1)
            } else if (nightIcon == 2){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_2)
            } else if (nightIcon == 3){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_3)
            } else if (nightIcon == 4){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_4)
            } else if (nightIcon == 5){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_5)
            } else if (nightIcon == 6){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_6)
            } else if (nightIcon == 7){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_7)
            } else if (nightIcon == 8){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_8)
            } else if (nightIcon == 11){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_11)
            } else if (nightIcon == 12){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_12)
            } else if (nightIcon == 13){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_13)
            } else if (nightIcon == 14){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_14)
            } else if (nightIcon == 15){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_15)
            } else if (nightIcon == 16){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_16)
            } else if (nightIcon == 17){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_17)
            } else if (nightIcon == 18){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_18)
            } else if (nightIcon == 20){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_20)
            } else if (nightIcon == 21){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_21)
            } else if (nightIcon == 22){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_22)
            } else if (nightIcon == 23){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_23)
            } else if (nightIcon == 24){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_24)
            } else if (nightIcon == 25){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_25)
            } else if (nightIcon == 26){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_26)
            } else if (nightIcon == 29){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_29)
            } else if (nightIcon == 30){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_30)
            } else if (nightIcon == 31){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_31)
            } else if (nightIcon == 32){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_32)
            } else if (nightIcon == 33){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_33)
            } else if (nightIcon == 34){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_34)
            } else if (nightIcon == 35){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_35)
            } else if (nightIcon == 36){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_36)
            } else if (nightIcon == 37){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_37)
            } else if (nightIcon == 38){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_38)
            } else if (nightIcon == 39){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_39)
            } else if (nightIcon == 40){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_40)
            } else if (nightIcon == 41){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_41)
            } else if (nightIcon == 42){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_42)
            } else if (nightIcon == 43){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_43)
            } else if (nightIcon == 44){
                binding.ivWeatherPhrase.setImageResource(R.drawable.ic_44)
            }
        }

    }
}