package com.harsh.testingapi

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.harsh.testingapi.DataClasses.AutoSearchCity
import com.harsh.testingapi.DataClasses.LocationSearchResponse
import com.harsh.testingapi.databinding.ActivityTabLayoutBinding
import com.harsh.testingapi.fragments.FiveDaysFragment
import com.harsh.testingapi.fragments.TodayFragment
import com.harsh.testingapi.fragments.TomorrowFragment
import com.harsh.testingapi.viewmodel.LocationViewModel
import com.harsh.testingapi.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.LocalTime
import java.util.Locale
import kotlin.math.roundToInt

class TabLayoutActivity : AppCompatActivity() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var autoSearchCity: AutoSearchCity
    private lateinit var adapter: ArrayAdapter<String>
    private val BASE_URL = "https://dataservice.accuweather.com/"
    private val API_KEY = "aSSGCxjROAmcZkVQv2rm70b1FGtALl1n"
    private lateinit var weatherApiService: WeatherApiService
    lateinit var binding: ActivityTabLayoutBinding
    var fragmentList = arrayListOf<Fragment>()
    val titleList = listOf("Today", "Tomorrow", "5 Days")
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationSearchResponse: LocationSearchResponse

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        getCurrentLocation()
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.locationKey.observe(this) { key ->
            // Handle the selected city key here
            Log.d("Got Key", "Got key: $key")
            // Call method to update UI or perform any other action using the selected city key
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherApiService = retrofit.create(WeatherApiService::class.java)




        binding.getCityName.setOnClickListener {

            val intent = Intent(this@TabLayoutActivity, SearchCityActivity::class.java)
            startActivity(intent)
        }




        fragmentList.add(TodayFragment())
        fragmentList.add(TomorrowFragment())
        fragmentList.add(FiveDaysFragment())
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.adapter = TabAdapter(this, fragmentList)

        // Link TabLayout with ViewPager
        TabLayoutMediator(binding.tlTabLayout, binding.viewPager) { tab, position ->
            tab.text = titleList[position] // Set tab titles
        }.attach()
       // fetchAutoSearchedCities()

        intent?.let {
            if(it.hasExtra("cityInfo")){
                var cityInfo = it.getStringExtra("cityInfo")
                autoSearchCity = Gson().fromJson(cityInfo, AutoSearchCity::class.java)
                sharedViewModel.setLocationKey(autoSearchCity.key)
            }else{
                //location
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.locationKey.observe(this) { key ->
            // Handle the selected city key here
            Log.d("Got Key resume", "Got key resume: $key")
            // Call method to update UI or perform any other action using the selected city key
        }
    }




    class TabAdapter(fa: FragmentActivity, private val fragmentList: List<Fragment>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount() = fragmentList.size

        override fun createFragment(position: Int) = fragmentList[position]
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation() {

        if (checkPermissions()) {

            if (isLocationEnabled()) {

                try {
                    fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show()
                            handleLocation(location.latitude, location.longitude)
                            Log.e("Tab LAT",location.latitude.toString())
                            Log.e("Tab LON",location.longitude.toString())
                            locationViewModel.setLocation(location.latitude, location.longitude)

                          //  fetchWeatherData(location.latitude, location.longitude) this function in fragment will be used to fetch data
                        }
                    }
                } catch (securityException: SecurityException) {
                    securityException.printStackTrace()

                    Toast.makeText(this, "SecurityException: ${securityException.message}", Toast.LENGTH_SHORT).show()
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

        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val cityName = addresses[0]?.locality
                if (cityName != null) {

                    Toast.makeText(this, "City name: $cityName", Toast.LENGTH_LONG).show()
                    //binding.getCityName.setText(cityName)
                } else {

                    Toast.makeText(this, "City name not available", Toast.LENGTH_SHORT).show()
                }
            } else {

                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
            }

        } catch (e: IOException) {
            e.printStackTrace()

            Toast.makeText(this, "Error getting city name", Toast.LENGTH_SHORT).show()
        }
    }


}