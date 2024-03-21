package com.harsh.testingapi.fragments
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.harsh.testingapi.DataClasses.DailyForecastResponse
import com.harsh.testingapi.DataClasses.ForecastRecyclerFiveDays
import com.harsh.testingapi.WeatherApiService
import com.harsh.testingapi.adapter.FiveDaysAdapter
import com.harsh.testingapi.databinding.FragmentFiveDaysBinding
import com.harsh.testingapi.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class FiveDaysFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentFiveDaysBinding
    private val BASE_URL = "https://dataservice.accuweather.com/"
    private val API_KEY = "ElQQjE7fc7McpPRkEWFIcI3IK8jXHvxG"
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var fiveDaysForecastResponse: DailyForecastResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiveDaysBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Initialize RecyclerView
        binding.rvFiveDays.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)

        // Observe location key changes
//old method
        sharedViewModel.locationKey.observe(viewLifecycleOwner, { locationKey ->
            if (locationKey != null) {
                fetchWeatherDataUsingLocationKey(locationKey)
                Log.e("Location key five days", locationKey)
            }
            if (locationKey == null) {
                Toast.makeText(requireContext(), "five null", Toast.LENGTH_SHORT).show()

        }
        })
//        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
//        sharedViewModel.selectedCityKey.observe(viewLifecycleOwner, { key ->
//            // Handle the selected city key here
//            Log.d("Got Key", "Selected key: $key")
//            // Call method to fetch weather data using the selected city key
//            fetchWeatherDataUsingLocationKey(key)
//        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherDataUsingLocationKey(locationKey: String) {
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
                        fiveDaysForecastResponse = forecastResponse.body()!!

                        val forecastDataList = mutableListOf<ForecastRecyclerFiveDays>()
                        val today = LocalDate.now()

                        for (forecast in fiveDaysForecastResponse.dailyForecasts) {
                            val forecastDate = LocalDate.parse(forecast.date.split("T")[0])
                            if (forecastDate >= today) {
                                val date = forecast.date
                                val maxTemperature = forecast.temperature.maximum.value.toString()
                                val minTemperature = forecast.temperature.minimum.value.toString()
                                val sunrise = forecast.sun.rise
                                val sunset = forecast.sun.set
                                val uvIndexValue = forecast.airAndPollen.firstOrNull { it.name == "UVIndex" }?.value.toString()
                                val uvIndexCategory = forecast.airAndPollen.firstOrNull { it.name == "UVIndex" }?.category.toString()
                                val dayIcon = forecast.day.icon
                                val dayPhrase = forecast.day.iconPhrase
                                val windSpeed = forecast.day.wind.speed.value.toString()

                                val forecastData = ForecastRecyclerFiveDays(
                                    date = date,
                                    maxTemperature = maxTemperature,
                                    minTemperature = minTemperature,
                                    sunrise = sunrise,
                                    sunset = sunset,
                                    uvIndexValue = uvIndexValue,
                                    uvIndexCategory = uvIndexCategory,
                                    dayIcon = dayIcon,
                                    dayPhrase = dayPhrase,
                                    windSpeed = windSpeed
                                )

                                forecastDataList.add(forecastData)
                            }
                        }

                        // Sort the forecastDataList by date if needed
                        forecastDataList.sortBy { it.date }

                        // Pass forecastDataList to the adapter
                        val adapter = FiveDaysAdapter(forecastDataList)
                        binding.rvFiveDays.adapter = adapter
                    } else {
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


}
