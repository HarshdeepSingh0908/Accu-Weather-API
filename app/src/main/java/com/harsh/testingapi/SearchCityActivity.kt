package com.harsh.testingapi
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.harsh.testingapi.DataClasses.AutoSearchCity
import com.harsh.testingapi.databinding.ActivitySearchCityBinding
import com.harsh.testingapi.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchCityActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivitySearchCityBinding
    private lateinit var adapter: ArrayAdapter<AutoSearchCity>
    private lateinit var listView: ListView
    private val BASE_URL = "https://dataservice.accuweather.com/"
    private val API_KEY = "ElQQjE7fc7McpPRkEWFIcI3IK8jXHvxG"
    private lateinit var weatherApiService: WeatherApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView = binding.lvCityLists
        listView.adapter = adapter

        // Initialize Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)

        // Handle search button click
        binding.getCityName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                searchCity()
                true
            } else {
                false
            }
        }

        // Handle Enter key press event
        binding.getCityName.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                searchCity()
                true
            } else {
                false
            }
        }

        // Handle list item click event
// Handle list item click event
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedCity = adapter.getItem(position)
            Log.e("TAG", " selectedCity $selectedCity")
//            val selectedInfo = selectedCity?.split("|")
           /* if (selectedInfo != null && selectedInfo.size >= 2) {
                val selectedName = selectedInfo[0]
                val selectedKey = selectedInfo[1]
                Log.d("key recieved", selectedKey)
                Log.d("name recieved", selectedName)
              //  sharedViewModel.setLocationKey(selectedKey)

                // Start TabLayoutActivity
                val intent = Intent(this, TabLayoutActivity::class.java)

                startActivity(intent)
            }*/

            var intent = Intent(this, TabLayoutActivity::class.java)
            intent.putExtra("cityInfo", Gson().toJson(selectedCity))
            startActivity(intent)

        }



    }

    private fun searchCity() {
        val query = binding.getCityName.text.toString()
        if (query.length >= 3) {
            // Perform API call for auto-suggested cities
            searchAutoCompleteCities(query) { AutoSearchCity ->
                var key = AutoSearchCity.key

            Log.e("searchcitykey",key)
                sharedViewModel.setLocationKey(key)//setSelectedCityKey
            }
        } else {
            // Show a toast or handle the case where the search query is too short
        }
    }

    private fun searchAutoCompleteCities(query: String, onCitySelected: (AutoSearchCity) -> Unit) {
        // API call to fetch auto-searched cities data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = weatherApiService.autocompleteCities(API_KEY, query, "en-us")
                if (response.isSuccessful) {
                    val cities: List<AutoSearchCity>? = response.body()
                    withContext(Dispatchers.Main) {
                        cities?.let {
//                            val cityInfo = it.map { city ->
//                                "${city.localizedName}| ${city.country.localizedName}" to city.key
//                            }
//                            val cityNames = cityInfo.map { it.first }
//                            val cityKeys = cityInfo.map { it.second }
                            adapter.clear()
                            adapter.addAll(cities)
                            adapter.notifyDataSetChanged()

                            // Call the callback function with the selected city name and key
                            if (cities.isNotEmpty()) {
//                                val selectedName = cityNames.first()
//                                val selectedKey = cityKeys.first()
//                                Log.e("Selected key before sending",selectedKey)
//                                Log.e("Selected name before sending",selectedName)
                                onCitySelected(cities[0])
                            }
                        }
                    }
                } else {
                    Log.e("AutoSearchedCity", "Failed to fetch data: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AutoSearchedCity", "Exception: ${e.message}")
            }
        }
    }


    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
