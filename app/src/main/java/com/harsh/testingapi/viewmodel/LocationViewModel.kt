package com.harsh.testingapi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harsh.testingapi.Constants

class LocationViewModel : ViewModel() {
    var hitApi = MutableLiveData<Boolean>(true)
    var map = MutableLiveData<Map<String, Double>>(mapOf())

    fun setLocation(latitude: Double, longitude: Double) {
        var latLongMap = HashMap<String, Double>()
        latLongMap.put(Constants.latitude, latitude)
        latLongMap.put(Constants.longitude, longitude)

    }
}