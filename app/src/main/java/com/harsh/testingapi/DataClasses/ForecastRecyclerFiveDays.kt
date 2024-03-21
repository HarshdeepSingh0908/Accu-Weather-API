package com.harsh.testingapi.DataClasses

data class ForecastRecyclerFiveDays(val date: String,
                                    val maxTemperature: String,
                                    val minTemperature: String,
                                    val sunrise: String,
                                    val sunset: String,
                                    val uvIndexValue: String,
                                    val uvIndexCategory: String,
                                    val dayIcon: Int,
                                    val dayPhrase: String,
                                    val windSpeed: String)
