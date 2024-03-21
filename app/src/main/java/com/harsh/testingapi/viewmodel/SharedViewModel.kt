package com.harsh.testingapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel : ViewModel() {
    private val _locationKey = MutableLiveData<String>()
    val locationKey: MutableLiveData<String>
        get() = _locationKey

    fun setLocationKey(key: String) {
        _locationKey.value = key
    }

}
