package com.harsh.testingapi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AutoSearchCityKeyViewModel : ViewModel() {
    val selectedCityKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    fun setSelectedCityKey(key: String) {
        selectedCityKey.value = key
    }
}