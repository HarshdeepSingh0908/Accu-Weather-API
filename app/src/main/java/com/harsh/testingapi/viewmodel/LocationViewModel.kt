package com.harsh.testingapi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()

    fun setLocation(latitude: Double, longitude: Double) {
        this.latitude.value = latitude
        this.longitude.value = longitude
    }
}