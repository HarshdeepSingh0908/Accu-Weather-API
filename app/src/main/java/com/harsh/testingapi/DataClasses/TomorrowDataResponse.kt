package com.harsh.testingapi

import com.google.gson.annotations.SerializedName

data class TomorrowDataResponse(
    val date: String,
    val epochDate: Long,
    val sun: SunData,
    val moon: MoonData,
    val temperature: TemperatureData,
    @SerializedName("realFeelTemperature")
    val realFeelTemperature: RealFeelTemperatureData,
    @SerializedName("realFeelTemperatureShade")
    val realFeelTemperatureShade: RealFeelTemperatureData,
    @SerializedName("hoursOfSun")
    val hoursOfSun: Double,
    @SerializedName("degreeDaySummary")
    val degreeDaySummary: DegreeDaySummaryData,
    val airAndPollen: List<AirAndPollenData>,
    val day: WeatherData,
    val night: WeatherData,
    val sources: List<String>,
    val mobileLink: String,
    val link: String
)

data class SunData(
    val rise: String,
    @SerializedName("epochRise")
    val epochRise: Long,
    val set: String,
    @SerializedName("epochSet")
    val epochSet: Long
)

data class MoonData(
    val rise: String,
    @SerializedName("epochRise")
    val epochRise: Long,
    val set: String,
    @SerializedName("epochSet")
    val epochSet: Long,
    val phase: String,
    val age: Int
)

data class TemperatureData(
    val minimum: ValueUnitData,
    val maximum: ValueUnitData
)

data class ValueUnitData(
    val value: Double,
    val unit: String,
    @SerializedName("unitType")
    val unitType: Int
)

data class RealFeelTemperatureData(
    val minimum: ValueUnitPhraseData,
    val maximum: ValueUnitPhraseData
)

data class ValueUnitPhraseData(
    val value: Double,
    val unit: String,
    @SerializedName("unitType")
    val unitType: Int,
    val phrase: String
)

data class DegreeDaySummaryData(
    val heating: ValueUnitData,
    val cooling: ValueUnitData
)

data class AirAndPollenData(
    val name: String,
    val value: Int,
    val category: String,
    @SerializedName("categoryValue")
    val categoryValue: Int,
    val type: String
)

data class WeatherData(
    val icon: Int,
    @SerializedName("iconPhrase")
    val iconPhrase: String,
    val hasPrecipitation: Boolean,
    val shortPhrase: String,
    val longPhrase: String,
    val precipitationProbability: Int,
    val thunderstormProbability: Int,
    val rainProbability: Int,
    val snowProbability: Int,
    val iceProbability: Int,
    val wind: WindData,
    @SerializedName("windGust")
    val windGust: WindData,
    val totalLiquid: ValueUnitData,
    val rain: ValueUnitData,
    val snow: ValueUnitData,
    val ice: ValueUnitData,
    val hoursOfPrecipitation: Double,
    val hoursOfRain: Double,
    val hoursOfSnow: Double,
    val hoursOfIce: Double,
    val cloudCover: Int,
    val evapotranspiration: ValueUnitData,
    val solarIrradiance: ValueUnitData,
    val relativeHumidity: HumidityData,
    val wetBulbTemperature: TemperatureData,
    val wetBulbGlobeTemperature: TemperatureData
)

data class WindData(
    val speed: ValueUnitData,
    val direction: DirectionData
)

data class DirectionData(
    val degrees: Int,
    val localized: String,
    val english: String
)

data class HumidityData(
    val minimum: Int,
    val maximum: Int,
    val average: Int
)
