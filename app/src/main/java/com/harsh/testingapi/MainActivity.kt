package com.harsh.testingapi

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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.harsh.testingapi.DataClasses.DailyForecastResponse
import com.harsh.testingapi.DataClasses.LocationSearchResponse
import com.harsh.testingapi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val BASE_URL = "https://dataservice.accuweather.com/"
    private val API_KEY = "X3b1pU0kl5OE3hlJ6KsmaZkHe9GuiIkm"
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var dailyForecastResponse: DailyForecastResponse
    private lateinit var locationSearchResponse: LocationSearchResponse
    private  val TAG = "MainActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)

//        handleLocation(31.0521, 76.1175)
//        fetchWeatherData(31.0521, 76.1175)
         getCurrentLocation()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                try {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            Toast.makeText(applicationContext, "Null Received", Toast.LENGTH_SHORT).show()

                        } else {
                           // Toast.makeText(this, "latitude:${location.latitude},longitude:${location.longitude}", Toast.LENGTH_SHORT).show()
                            handleLocation(location.latitude, location.longitude)
                          //  handleLocation(31.05, 76.11)
                            fetchWeatherData(location.latitude, location.longitude)
                          //  fetchWeatherData(31.05, 76.11)
                        }
                    }
                } catch (securityException: SecurityException) {
                    securityException.printStackTrace()

                    Toast.makeText(applicationContext, "SecurityException: ${securityException.message}", Toast.LENGTH_SHORT).show()
                }
            } else {

                Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {

            requestPermission()
        }
    }

    private fun handleLocation(latitude: Double, longitude: Double) {

        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val cityName = addresses[0]?.locality
                if (cityName != null) {

                    Toast.makeText(applicationContext, "City name: $cityName", Toast.LENGTH_LONG).show()
                    binding.getCityName.setText(cityName)
                } else {

                    Toast.makeText(applicationContext, "City name not available", Toast.LENGTH_SHORT).show()
                }
            } else {

                Toast.makeText(applicationContext, "No address found", Toast.LENGTH_SHORT).show()
            }

        } catch (e: IOException) {
            e.printStackTrace()

            Toast.makeText(applicationContext, "Error getting city name", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val query = "$latitude,$longitude"
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val locationResponse = weatherApiService.searchLocation(API_KEY, query,true)
                if (locationResponse.isSuccessful) {
                    locationSearchResponse = locationResponse.body()!!
                    val locationKey = locationSearchResponse.Key
                    val locationname = locationSearchResponse.LocalizedName
                    Log.e(TAG, " locationname ${locationname}")
                    binding.getCityName.setText(locationname)
                    if (!locationKey.isNullOrBlank()) {
                        val forecastResponse = weatherApiService.getDailyForecast(locationKey, API_KEY, true)
                        if (forecastResponse.isSuccessful) {
                            Log.e(TAG, " forecastResponse ${forecastResponse.body()}")
                            dailyForecastResponse = forecastResponse.body()!!
                            Log.e(TAG, " dailyForecast response ${dailyForecastResponse.headline}")

                            Log.d("WeatherFetch", "API Response: $dailyForecastResponse")


                            val TemperatureMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.minimum?.value
                            Log.e(TAG," TemperatureMin $TemperatureMin")
                            val TemperatureMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.temperature?.maximum?.value
                            Log.e(TAG," TemperatureMin $TemperatureMax")
                            val realFeelTempMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.maximum?.value?.toFloat()
                            val realFeelTempMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.minimum?.value?.toFloat()
                            Log.e(TAG," TemperatureMin ${dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature}")

                            val realFeelTemperatureMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.minimum?.value
                            val realFeelTemperatureMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.realFeelTemperature?.maximum?.value
                            val humidityMin = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.relativeHumidity?.minimum
                            val humidityMax = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.relativeHumidity?.maximum
                            val headline = dailyForecastResponse.dailyForecasts?.firstOrNull()?.day?.iconPhrase
                            val sunrise = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.rise.toString()
                            val convertedSunrise = convertToCurrentLocalTime(sunrise)
                            val sunset = dailyForecastResponse.dailyForecasts?.firstOrNull()?.sun?.set.toString()
                            val convertedSunset = convertToCurrentLocalTime(sunset)
                            val date = dailyForecastResponse.headline?.effectiveDate
                            val datee = convertToCurrentLocalTime2(date.toString())
                            val temperature = TemperatureMax?.toFloat()
                                ?.let { fahrenheitToCelsius(it) }

                            //setting data on views
                            val feelsLikeCelcius = realFeelTempMin?.let { fahrenheitToCelsius(it).roundToInt() }
                            binding.tvFeelsLike.text = "Feels Like "+feelsLikeCelcius.toString()+"°C"
                            binding.tvHumidity.text = humidityMax.toString()
                            binding.tvSunrise.text = convertedSunrise
                            binding.tvSunset.text = convertedSunset
                            binding.tvTempFaranhite.text = TemperatureMax.toString()
                            binding.tvWeatherType.text = headline.toString()
                            binding.tvDateAndTime.text = "Date:"+datee
                            binding.tvTemp.text = temperature?.roundToInt().toString()+"°C"
//                            Log.e(TAG, " dailyForecastResponse.DailyForecasts.firstOrNull() ${ dailyForecastResponse.DailyForecasts.firstOrNull()}")
                            Log.d("WeatherFetch", "Minimum Real Feel Temperature: ${realFeelTemperatureMin ?: "N/A"}")
                            Log.d("WeatherFetch", "Maximum Real Feel Temperature: ${realFeelTemperatureMax ?: "N/A"}")
                            Log.d("WeatherFetch", "Minimum Humidity: ${humidityMin ?: "N/A"}")
                            Log.d("WeatherFetch", "Maximum Humidity: ${humidityMax ?: "N/A"}")
                        } else {

                            Log.e("WeatherFetch", "Failed to fetch weather data: ${forecastResponse.message()}")
                            Toast.makeText(applicationContext, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                        }
                    } else {

                        Log.e("WeatherFetch", "Location key is null or empty")
                        Toast.makeText(applicationContext, "Location key is null or empty", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    Log.e("WeatherFetch", "Failed to fetch location data: ${locationResponse.message()}")
                    Toast.makeText(applicationContext, "Failed to fetch location data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {

                Log.e("WeatherFetch", "Error: ${e.message}")
                Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToCurrentLocalTime(givenTime: String): String {
        // Parse the given time string
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val dateTime = LocalDateTime.parse(givenTime, formatter)

        // Convert to ZonedDateTime with the given timezone
        val givenZone = ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(5, 30))
        val zonedDateTime = ZonedDateTime.of(dateTime, givenZone)

        // Convert to current local time
        val localZone = ZoneId.systemDefault()
        val localDateTime = zonedDateTime.withZoneSameInstant(localZone).toLocalDateTime()

        // Format the local time
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }@RequiresApi(Build.VERSION_CODES.O)
    fun convertToCurrentLocalTime2(givenTime: String): String {
        // Parse the given time string
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val dateTime = LocalDateTime.parse(givenTime, formatter)

        // Convert to ZonedDateTime with the given timezone
        val givenZone = ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(5, 30))
        val zonedDateTime = ZonedDateTime.of(dateTime, givenZone)

        // Convert to current local time
        val localZone = ZoneId.systemDefault()
        val localDateTime = zonedDateTime.withZoneSameInstant(localZone).toLocalDateTime()

        // Format the local time
        return localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }
    fun fahrenheitToCelsius(fahrenheit: Float): Float {
        return (fahrenheit - 32) * 5 / 9
    }

}
