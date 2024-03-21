package com.harsh.testingapi.DataClasses
import com.google.gson.annotations.SerializedName

data class FiveDaysForecastResponse(
    @SerializedName("Headline") val headline: Headline,
    @SerializedName("DailyForecasts") val dailyForecasts: List<DailyForecast>
)



data class DailyForecast(
    @SerializedName("Date") val date: String,
    @SerializedName("EpochDate") val epochDate: Long,
    @SerializedName("Sun") val sun: Sun,
    @SerializedName("Moon") val moon: Moon,
    @SerializedName("Temperature") val temperature: Temperature,
    @SerializedName("RealFeelTemperature") val realFeelTemperature: RealFeelTemperature,
    @SerializedName("RealFeelTemperatureShade") val realFeelTemperatureShade: RealFeelTemperatureShade,
    @SerializedName("HoursOfSun") val hoursOfSun: Double,
    @SerializedName("DegreeDaySummary") val degreeDaySummary: DegreeDaySummary,
    @SerializedName("AirAndPollen") val airAndPollen: List<AirAndPollen>,
    @SerializedName("Day") val day: Day,
    @SerializedName("Night") val night: Night,
    @SerializedName("Sources") val sources: List<String>,
    @SerializedName("MobileLink") val mobileLink: String,
    @SerializedName("Link") val link: String
)

data class RealFeelTemperature(
    @SerializedName("Minimum") val minimum: ValuePhraseUnit,
    @SerializedName("Maximum") val maximum: ValuePhraseUnit
)

data class RealFeelTemperatureShade(
    @SerializedName("Minimum") val minimum: ValuePhraseUnit,
    @SerializedName("Maximum") val maximum: ValuePhraseUnit
)
data class ValuePhraseUnit(
    @SerializedName("Value") val value: Int,
    @SerializedName("Unit") val unit: String,
    @SerializedName("UnitType") val unitType: Int,
    @SerializedName("Phrase") val phrase: String
)


