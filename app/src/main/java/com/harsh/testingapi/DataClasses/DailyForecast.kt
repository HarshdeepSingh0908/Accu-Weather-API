package com.harsh.testingapi.DataClasses

data class DailyForecast(
    val Date: String,
    val Sun: Sun?,
    val Moon: Moon?,
    val Temperature: Temperature,
    val RealFeelTemperature: RealFeelTemperature?,
    val RealFeelTemperatureShade: RealFeelTemperature?,
    val HoursOfSun: Double,
    val DegreeDaySummary: DegreeDaySummary?,
    val AirAndPollen: List<AirAndPollen>?,
    val Day: Day,
    val Night: Night
)
