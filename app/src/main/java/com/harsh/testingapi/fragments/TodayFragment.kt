package com.harsh.testingapi.fragments


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.harsh.testingapi.Constants
import com.harsh.testingapi.DataClasses.DailyForecastResponse
import com.harsh.testingapi.DataClasses.LocationSearchResponse
import com.harsh.testingapi.R
import com.harsh.testingapi.WeatherApiService
import com.harsh.testingapi.databinding.FragmentTodayBinding
import com.harsh.testingapi.viewmodel.LocationViewModel
import com.harsh.testingapi.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodayFragment : Fragment() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var sharedViewModel: SharedViewModel

    lateinit var binding : FragmentTodayBinding
    private val BASE_URL = "https://dataservice.accuweather.com/"
    private val API_KEY = "aSSGCxjROAmcZkVQv2rm70b1FGtALl1n"
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var dailyForecastResponse: DailyForecastResponse
    private lateinit var locationSearchResponse: LocationSearchResponse
    private  val TAG = "WeatherFragment"
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.locationKey.observe(viewLifecycleOwner, { key ->
            // Handle the selected city key here
            Log.d("Got Key", "got key: $key")
            // Call method to fetch weather data using the selected city key
            fetchWeatherDataUsingSearch(key)
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTodayBinding.inflate(layoutInflater)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
        locationViewModel.map.observe(viewLifecycleOwner){ it ->
            Log.d("Latitude", "Latitude changed: $it")
                if(locationViewModel.hitApi.value == false)
                    fetchWeatherData(it[Constants.latitude]?:0.0, it[Constants.longitude]?:0.0)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)

    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getCurrentLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                try {
//                    fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
//                        val location: Location? = task.result
//                        if (location == null) {
//                            Toast.makeText(requireContext(), "Null Received", Toast.LENGTH_SHORT).show()
//
//                        } else {
//                            handleLocation(location.latitude, location.longitude)
//                            fetchWeatherData(location.latitude, location.longitude)
//                        }
//                    }
//                } catch (securityException: SecurityException) {
//                    securityException.printStackTrace()
//
//                    Toast.makeText(requireContext(), "SecurityException: ${securityException.message}", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//
//                Toast.makeText(requireContext(), "Turn on Location", Toast.LENGTH_SHORT).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//
//            requestPermission()
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val query = "$latitude,$longitude"
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val locationResponse = weatherApiService.searchLocation(API_KEY, query,true)
                if (locationResponse.isSuccessful) {
                    locationSearchResponse = locationResponse.body()!!
                    val locationKey = locationSearchResponse.Key
                    sharedViewModel.setLocationKey(locationKey)
                    val locationname = locationSearchResponse.LocalizedName
                    Log.e(TAG, " locationname ${locationname}")
                  //  binding.getCityName.setText(locationname)
                    if (!locationKey.isNullOrBlank()) {
                        val forecastResponse = weatherApiService.getDailyForecast(locationKey, API_KEY, true)
                        if (forecastResponse.isSuccessful) {
                          //  Log.e(TAG, " forecastResponse ${forecastResponse.body()}")
                            dailyForecastResponse = forecastResponse.body()!!
                         //   Log.e(TAG, " dailyForecast response ${dailyForecastResponse.headline}")

                         //   Log.d("WeatherFetch", "API Response: $dailyForecastResponse")


                            val sunrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.rise.toString()
                            val sunset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.set.toString()
                            // Inside onViewCreated or fetchWeatherData method after fetching sunrise and sunset times
                            val currentTime = LocalTime.now() // Get current time
                            val sunriseTime = LocalTime.parse(sunrise.substring(11, 16)) // Extract time portion and parse sunrise time
                            val sunsetTime = LocalTime.parse(sunset.substring(11, 16)) // Extract time portion and parse sunset time


                            val isDayTime = currentTime.isAfter(sunriseTime) && currentTime.isBefore(sunsetTime)
                            Log.e("Current Time:",currentTime.toString())
                            Log.e("Sunrise Time:",sunriseTime.toString())
                            Log.e("Sunset Time:",sunsetTime.toString())
                            if (isDayTime) {
                                // Set data for day
                                // For example:
                                val TemperatureMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val iconDay = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.icon
                                Log.e("DayIcon",iconDay.toString())
                                setIcon(iconDay)
                                val TemperatureMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value?.toFloat()
                                val realFeelTempMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.minimum?.value?.toFloat()
                                val dayHighTemp = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val dayLowTemp = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value?.toFloat()
                                val realFeelTempMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.maximum?.value?.toFloat()

                                val humidityMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.relativeHumidity?.maximum.toString()
                                val headline = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.iconPhrase
                                //   val sunrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.rise.toString()
                                Log.e("Sunrise:",sunrise)
                                val convertedSunrise = getTimeFromDateString(sunrise)

                                // val sunset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.set.toString()
                                val convertedSunset = getTimeFromDateString(sunset)
                                Log.e("Sunrise,Sunset:",convertedSunrise+convertedSunset)

                                val date = dailyForecastResponse.headline?.effectiveDate
                                val formattedDate = convertToCurrentLocalTime(date.toString())
                                val temperature = TemperatureMax?.let { fahrenheitToCelsius(it).roundToInt() }

                                //setting data on views
                                val dayLow = dayLowTemp?.let { fahrenheitToCelsius(it).roundToInt() }
                                val dayHigh = dayHighTemp?.let { fahrenheitToCelsius(it).roundToInt() }
                                val sunLight = dailyForecastResponse.dailyForecasts?.firstOrNull()?.hoursOfSun.toString()
                                val windSpeed = dailyForecastResponse?.dailyForecasts?.firstOrNull()?.day?.wind?.speed?.value.toString()
                                val windSpeedUnit = dailyForecastResponse?.dailyForecasts?.firstOrNull()?.day?.wind?.speed?.unit.toString()
                                val uvIndex = dailyForecastResponse?.dailyForecasts?.firstOrNull()?.airAndPollen?.find { it.name == "UVIndex" }?.value
                                val uvIndexInfo = dailyForecastResponse.dailyForecasts.firstOrNull()?.airAndPollen?.find { it.name == "UVIndex" }
                                val airQualityInfo = dailyForecastResponse.dailyForecasts.firstOrNull()?.airAndPollen?.find { it.name == "AirQuality" }
                                val uvIndexValue = uvIndexInfo?.value.toString()
                                val uvIndexCategory = uvIndexInfo?.category
                                val airQualityValue = airQualityInfo?.value.toString()
                                val airQualityCategory = airQualityInfo?.category
                                binding.tvHighestTemp.text = "High "+dayHigh.toString()+"° ↑"
                                binding.tvLowestTemp.text = "Low "+dayLow.toString()+"° ↓"
                                binding.tvDateAndTime.text = formattedDate
                                binding.tvMainTemp.text = temperature.toString()
                                binding.tvPhrase.text = headline
                                binding.tvHumidity.text = "Humidity:\n"+humidityMax+"% "
                                binding.tvSunriseTime.text = "Sunrise:\n"+convertedSunrise
                                binding.tvSunsetTime.text = "Sunset:\n"+convertedSunset
                                binding.tvHoursOfSun.text = "Sunlight: "+sunLight+" Hours"
                                binding.tvUVIndex.text = "UV Index: "+uvIndexValue+"\n"+uvIndexCategory
                                binding.tvAirQuality.text = "Air Quality: "+airQualityValue+"\n"+airQualityCategory
                                binding.tvWindspeed.text = "Windspeed:\n"+windSpeed+" mi/h"


                            }
                            else {
                                // Set data for night
                                // For example:
                               // binding.textViewWeatherInfo.text = "Nighttime Weather Information"
                                binding.llUvindex.visibility = View.GONE
                                binding.llAirquality.visibility = View.GONE
                                val nightForecast = dailyForecastResponse.dailyForecasts?.firstOrNull()?.night


                                val nightIcon = nightForecast?.icon
                                Log.e("nightIconnnnnn",nightIcon.toString())
                                val nightIconPhrase = nightForecast?.iconPhrase
                                val nightPrecipitationProbability = nightForecast?.precipitationProbability
                                val nightThunderstormProbability = nightForecast?.thunderstormProbability
                                val nightRainProbability = nightForecast?.rainProbability
                                val nightSnowProbability = nightForecast?.snowProbability
                                val nightIceProbability = nightForecast?.iceProbability
                                val nightWindSpeed = nightForecast?.wind?.speed?.value
                                val nightWindSpeedUnit = nightForecast?.wind?.speed?.unit
                                val nightWindDirection = nightForecast?.wind?.direction
                                val nightWindGustSpeed = nightForecast?.windgust?.speed
                                val nightWindGustDirection = nightForecast?.windgust?.direction
                                val nightTotalLiquid = nightForecast?.totalLiquid
                                val nightRain = nightForecast?.rain
                                val nightSnow = nightForecast?.snow
                                val nightIce = nightForecast?.ice
                                val nightHoursOfPrecipitation = nightForecast?.hoursOfPrecipitation
                                val nightHoursOfRain = nightForecast?.hoursOfRain
                                val nightHoursOfSnow = nightForecast?.hoursOfSnow
                                val nightHoursOfIce = nightForecast?.hoursOfIce
                                val nightCloudCover = nightForecast?.cloudCover
                                val nightEvapotranspiration = nightForecast?.evapotranspiration
                                val nightSolarIrradiance = nightForecast?.solarIrradiance
                                val nightRelativeHumidityMinimum = nightForecast?.relativeHumidity?.minimum
                                val nightRelativeHumidityMaximum = nightForecast?.relativeHumidity?.maximum
                                val nightRelativeHumidityAverage = nightForecast?.relativeHumidity?.average
                                val nightWetBulbTemperatureMinimum = nightForecast?.wetBulbTemperature?.minimum?.value
                                val nightWetBulbTemperatureMaximum = nightForecast?.wetBulbTemperature?.maximum?.value
                                val nightWetBulbTemperatureAverage = nightForecast?.wetBulbTemperature?.average?.value
                                val nightWetBulbGlobeTemperatureMinimum = nightForecast?.wetBulbGlobeTemperature?.minimum?.value
                                val nightWetBulbGlobeTemperatureMaximum = nightForecast?.wetBulbGlobeTemperature?.maximum?.value
                                val nightWetBulbGlobeTemperatureAverage = nightForecast?.wetBulbGlobeTemperature?.average?.value
                                val moonrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.moon?.rise.toString()
                                val moonset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.moon?.set.toString()
                                val convertedMoonrise = getTimeFromDateString(moonrise)
                                val convertedMoonSet = getTimeFromDateString(moonset)
                                val TemperatureMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val temperatureInCels = TemperatureMax?.let { fahrenheitToCelsius(it).roundToInt() }
                                val minNight = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value?.toFloat()
                                val maxNight = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val date = dailyForecastResponse.headline?.effectiveDate
                                val formattedDate = convertToCurrentLocalTime(date.toString())
                                setIcon(nightIcon)
                                Log.e("Moonset",moonset)
                                binding.tvDateAndTime.text = formattedDate
                                binding.tvMainTemp.text = temperatureInCels.toString()+""
                                binding.tvPhrase.text = nightIconPhrase
                                binding.ivSunrise.setImageResource(R.drawable.moonrise)
                                binding.ivSunset.setImageResource(R.drawable.moonset)
                                binding.tvWindspeed.text = "Windspeed:\n"+nightWindSpeed+" "+nightWindSpeedUnit
                                binding.tvHumidity.text = "Humidity:\n"+nightRelativeHumidityAverage+"%"
                                binding.tvSunriseTime.text = "Moonrise:\n"+convertedMoonrise
                                binding.tvSunsetTime.text = "Moonset:\n"+convertedMoonSet
                                val nightLow = minNight?.let { fahrenheitToCelsius(it).roundToInt() }
                                val nightHigh = maxNight?.let { fahrenheitToCelsius(it).roundToInt() }
                                binding.tvHighestTemp.text = "High "+nightHigh.toString()+"° ↑"
                                binding.tvLowestTemp.text = "Low "+nightLow.toString()+"° ↓"

                            }
                        } else {

                            Log.e("WeatherFetch", "Failed to fetch weather data: ${forecastResponse.message()}")
                            Toast.makeText(requireContext(), "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                        }
                    } else {

                        Toast.makeText(requireContext(), "Location key not found", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    Log.e("WeatherFetch", "Failed to fetch location data: ${locationResponse.message()}")
                    Toast.makeText(requireContext(), "Failed to fetch location data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {

                Log.e("WeatherFetch", "Exception: ${e.message}")
                Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherDataUsingSearch(cityKey : String) {

        CoroutineScope(Dispatchers.Main).launch {
            try {
                    if (!cityKey.isNullOrBlank()) {
                        val forecastResponse = weatherApiService.getDailyForecast(cityKey, API_KEY, true)
                        if (forecastResponse.isSuccessful) {
                          //  Log.e(TAG, " forecastResponse ${forecastResponse.body()}")
                            dailyForecastResponse = forecastResponse.body()!!
                         //   Log.e(TAG, " dailyForecast response ${dailyForecastResponse.headline}")

                            Log.d("WeatherFetch", "Key Response: $dailyForecastResponse")


                            val sunrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.rise.toString()
                            val sunset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.set.toString()
                            // Inside onViewCreated or fetchWeatherData method after fetching sunrise and sunset times
                            val currentTime = LocalTime.now() // Get current time
                            val sunriseTime = LocalTime.parse(sunrise.substring(11, 16)) // Extract time portion and parse sunrise time
                            val sunsetTime = LocalTime.parse(sunset.substring(11, 16)) // Extract time portion and parse sunset time


                            val isDayTime = currentTime.isAfter(sunriseTime) && currentTime.isBefore(sunsetTime)
                            Log.e("Current Time:",currentTime.toString())
                            Log.e("Sunrise Time:",sunriseTime.toString())
                            Log.e("Sunset Time:",sunsetTime.toString())
                            if (isDayTime) {
                                // Set data for day
                                // For example:
                                val TemperatureMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val iconDay = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.icon
                                Log.e("DayIcon",iconDay.toString())
                                setIcon(iconDay)
                                val TemperatureMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value?.toFloat()
                                val realFeelTempMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.minimum?.value?.toFloat()
                                val dayHighTemp = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val dayLowTemp = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value?.toFloat()
                                val realFeelTempMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.maximum?.value?.toFloat()

                                val humidityMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.relativeHumidity?.maximum.toString()
                                val headline = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.iconPhrase
                                //   val sunrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.rise.toString()
                                Log.e("Sunrise:",sunrise)
                                val convertedSunrise = getTimeFromDateString(sunrise)

                                // val sunset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.set.toString()
                                val convertedSunset = getTimeFromDateString(sunset)
                                Log.e("Sunrise,Sunset:",convertedSunrise+convertedSunset)

                                val date = dailyForecastResponse.headline?.effectiveDate
                                val formattedDate = convertToCurrentLocalTime(date.toString())
                                val temperature = TemperatureMax?.let { fahrenheitToCelsius(it).roundToInt() }

                                //setting data on views
                                val dayLow = dayLowTemp?.let { fahrenheitToCelsius(it).roundToInt() }
                                val dayHigh = dayHighTemp?.let { fahrenheitToCelsius(it).roundToInt() }
                                val sunLight = dailyForecastResponse.dailyForecasts?.firstOrNull()?.hoursOfSun.toString()
                                val windSpeed = dailyForecastResponse?.dailyForecasts?.firstOrNull()?.day?.wind?.speed?.value.toString()
                                val windSpeedUnit = dailyForecastResponse?.dailyForecasts?.firstOrNull()?.day?.wind?.speed?.unit.toString()
                                val uvIndex = dailyForecastResponse?.dailyForecasts?.firstOrNull()?.airAndPollen?.find { it.name == "UVIndex" }?.value
                                val uvIndexInfo = dailyForecastResponse.dailyForecasts.firstOrNull()?.airAndPollen?.find { it.name == "UVIndex" }
                                val airQualityInfo = dailyForecastResponse.dailyForecasts.firstOrNull()?.airAndPollen?.find { it.name == "AirQuality" }
                                val uvIndexValue = uvIndexInfo?.value.toString()
                                val uvIndexCategory = uvIndexInfo?.category
                                val airQualityValue = airQualityInfo?.value.toString()
                                val airQualityCategory = airQualityInfo?.category
                                binding.tvHighestTemp.text = "High "+dayHigh.toString()+"° ↑"
                                binding.tvLowestTemp.text = "Low "+dayLow.toString()+"° ↓"
                                binding.tvDateAndTime.text = formattedDate
                                binding.tvMainTemp.text = temperature.toString()
                                binding.tvPhrase.text = headline
                                binding.tvHumidity.text = "Humidity:\n"+humidityMax+"% "
                                binding.tvSunriseTime.text = "Sunrise:\n"+convertedSunrise
                                binding.tvSunsetTime.text = "Sunset:\n"+convertedSunset
                                binding.tvHoursOfSun.text = "Sunlight: "+sunLight+" Hours"
                                binding.tvUVIndex.text = "UV Index: "+uvIndexValue+"\n"+uvIndexCategory
                                binding.tvAirQuality.text = "Air Quality: "+airQualityValue+"\n"+airQualityCategory
                                binding.tvWindspeed.text = "Windspeed:\n"+windSpeed+" mi/h"


                            } else {
                                // Set data for night
                                // For example:
                               // binding.textViewWeatherInfo.text = "Nighttime Weather Information"
                                binding.llUvindex.visibility = View.GONE
                                binding.llAirquality.visibility = View.GONE
                                val nightForecast = dailyForecastResponse.dailyForecasts?.firstOrNull()?.night


                                val nightIcon = nightForecast?.icon
                                Log.e("nightIconnnnnn",nightIcon.toString())
                                val nightIconPhrase = nightForecast?.iconPhrase
                                val nightPrecipitationProbability = nightForecast?.precipitationProbability
                                val nightThunderstormProbability = nightForecast?.thunderstormProbability
                                val nightRainProbability = nightForecast?.rainProbability
                                val nightSnowProbability = nightForecast?.snowProbability
                                val nightIceProbability = nightForecast?.iceProbability
                                val nightWindSpeed = nightForecast?.wind?.speed?.value
                                val nightWindSpeedUnit = nightForecast?.wind?.speed?.unit
                                val nightWindDirection = nightForecast?.wind?.direction
                                val nightWindGustSpeed = nightForecast?.windgust?.speed
                                val nightWindGustDirection = nightForecast?.windgust?.direction
                                val nightTotalLiquid = nightForecast?.totalLiquid
                                val nightRain = nightForecast?.rain
                                val nightSnow = nightForecast?.snow
                                val nightIce = nightForecast?.ice
                                val nightHoursOfPrecipitation = nightForecast?.hoursOfPrecipitation
                                val nightHoursOfRain = nightForecast?.hoursOfRain
                                val nightHoursOfSnow = nightForecast?.hoursOfSnow
                                val nightHoursOfIce = nightForecast?.hoursOfIce
                                val nightCloudCover = nightForecast?.cloudCover
                                val nightEvapotranspiration = nightForecast?.evapotranspiration
                                val nightSolarIrradiance = nightForecast?.solarIrradiance
                                val nightRelativeHumidityMinimum = nightForecast?.relativeHumidity?.minimum
                                val nightRelativeHumidityMaximum = nightForecast?.relativeHumidity?.maximum
                                val nightRelativeHumidityAverage = nightForecast?.relativeHumidity?.average
                                val nightWetBulbTemperatureMinimum = nightForecast?.wetBulbTemperature?.minimum?.value
                                val nightWetBulbTemperatureMaximum = nightForecast?.wetBulbTemperature?.maximum?.value
                                val nightWetBulbTemperatureAverage = nightForecast?.wetBulbTemperature?.average?.value
                                val nightWetBulbGlobeTemperatureMinimum = nightForecast?.wetBulbGlobeTemperature?.minimum?.value
                                val nightWetBulbGlobeTemperatureMaximum = nightForecast?.wetBulbGlobeTemperature?.maximum?.value
                                val nightWetBulbGlobeTemperatureAverage = nightForecast?.wetBulbGlobeTemperature?.average?.value
                                val moonrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.moon?.rise.toString()
                                val moonset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.moon?.set.toString()
                                val convertedMoonrise = getTimeFromDateString(moonrise)
                                val convertedMoonSet = getTimeFromDateString(moonset)
                                val TemperatureMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val temperatureInCels = TemperatureMax?.let { fahrenheitToCelsius(it).roundToInt() }
                                val minNight = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value?.toFloat()
                                val maxNight = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value?.toFloat()
                                val date = dailyForecastResponse.headline?.effectiveDate
                                val formattedDate = convertToCurrentLocalTime(date.toString())
                                setIcon(nightIcon)
                                Log.e("Moonset",moonset)
                                binding.tvDateAndTime.text = formattedDate
                                binding.tvMainTemp.text = temperatureInCels.toString()+""
                                binding.tvPhrase.text = nightIconPhrase
                                binding.ivSunrise.setImageResource(R.drawable.moonrise)
                                binding.ivSunset.setImageResource(R.drawable.moonset)
                                binding.tvWindspeed.text = "Windspeed:\n"+nightWindSpeed+" "+nightWindSpeedUnit
                                binding.tvHumidity.text = "Humidity:\n"+nightRelativeHumidityAverage+"%"
                                binding.tvSunriseTime.text = "Moonrise:\n"+convertedMoonrise
                                binding.tvSunsetTime.text = "Moonset:\n"+convertedMoonSet
                                val nightLow = minNight?.let { fahrenheitToCelsius(it).roundToInt() }
                                val nightHigh = maxNight?.let { fahrenheitToCelsius(it).roundToInt() }
                                binding.tvHighestTemp.text = "High "+nightHigh.toString()+"° ↑"
                                binding.tvLowestTemp.text = "Low "+nightLow.toString()+"° ↓"
                            }
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

    private fun setIcon(nightIcon: Int?) {
        if (nightIcon in 1..44){
            var name = "ic_$nightIcon"
        }
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TodayFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_CITY_KEY = "city_key"

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


}