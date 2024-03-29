package com.harsh.testingapi.DataClasses


import com.google.gson.annotations.SerializedName


data class DailyForecasts(
    @SerializedName("Date") val date: String,
    @SerializedName("EpochDate") val epochDate: Long,
    @SerializedName("Sun") val sun: Sun,
    @SerializedName("Moon") val moon: Moon,
    @SerializedName("Temperature") val temperature: Temperature,
    @SerializedName("RealFeelTemperature") val realFeelTemperature: Temperature,
    @SerializedName("RealFeelTemperatureShade") val realFeelTemperatureShade: Temperature,
    @SerializedName("HoursOfSun") val hoursOfSun: Double,
    @SerializedName("DegreeDaySummary") val degreeDaySummary: DegreeDaySummary,
    @SerializedName("AirAndPollen") val airAndPollen: List<AirAndPollen>,
    @SerializedName("Day") val day: Day,
    @SerializedName("Night") val night: Night,
    @SerializedName("Sources") val sources: List<String>,
    @SerializedName("MobileLink") val mobileLink: String,
    @SerializedName("Link") val link: String
)
data class Headline(
    @SerializedName("EffectiveDate") val effectiveDate: String,
    @SerializedName("EffectiveEpochDate") val effectiveEpochDate: Long,
    @SerializedName("Severity") val severity: Int,
    @SerializedName("Text") val text: String,
    @SerializedName("Category") val category: String,
    @SerializedName("EndDate") val endDate: String,
    @SerializedName("EndEpochDate") val endEpochDate: Long,
    @SerializedName("MobileLink") val mobileLink: String,
    @SerializedName("Link") val link: String

)

data class Sun(
    @SerializedName("Rise") val rise: String,
    @SerializedName("EpochRise") val epochRise: Long,
    @SerializedName("Set") val set: String,
    @SerializedName("EpochSet") val epochSet: Long
)

data class Moon(
    @SerializedName("Rise") val rise: String,
    @SerializedName("EpochRise") val epochRise: Long,
    @SerializedName("Set") val set: String,
    @SerializedName("EpochSet") val epochSet: Long,
    @SerializedName("Phase") val phase: String,
    @SerializedName("Age") val age: Int
)

data class Temperature(
    @SerializedName("Minimum") val minimum: ValueUnit,
    @SerializedName("Maximum") val maximum: ValueUnit,
    @SerializedName("Average") val average: ValueUnit
)

data class ValueUnit(
    @SerializedName("Value") val value: Double,
    @SerializedName("Speed") val speed: Double,
    @SerializedName("Unit") val unit: String,
    @SerializedName("UnitType") val unitType: Int,
    @SerializedName("Phrase") val phrase: String? = null
)

data class Wind(
    @SerializedName("Speed") val speed: ValueUnit,
    @SerializedName("Direction") val direction: Direction
)
data class Windgust(
    @SerializedName("Speed") val speed: ValueUnit,
    @SerializedName("Direction") val direction: Direction
)

data class Direction(
    @SerializedName("Degrees") val degrees: Int,
    @SerializedName("Localized") val localized: String,
    @SerializedName("English") val english: String
)

data class Day(
    @SerializedName("Icon") val icon: Int,
    @SerializedName("IconPhrase") val iconPhrase: String,
    @SerializedName("HasPrecipitation") val hasPrecipitation: Boolean,
    @SerializedName("ShortPhrase") val shortPhrase: String,
    @SerializedName("LongPhrase") val longPhrase: String,
    @SerializedName("PrecipitationProbability") val precipitationProbability: Int,
    @SerializedName("ThunderstormProbability") val thunderstormProbability: Int,
    @SerializedName("RainProbability") val rainProbability: Int,
    @SerializedName("SnowProbability") val snowProbability: Int,
    @SerializedName("IceProbability") val iceProbability: Int,
    @SerializedName("Wind") val wind: Wind,
    @SerializedName("TotalLiquid") val totalLiquid: ValueUnit,
    @SerializedName("Rain") val rain: ValueUnit,
    @SerializedName("Snow") val snow: ValueUnit,
    @SerializedName("Ice") val ice: ValueUnit,
    @SerializedName("HoursOfPrecipitation") val hoursOfPrecipitation: Double,
    @SerializedName("HoursOfRain") val hoursOfRain: Double,
    @SerializedName("HoursOfSnow") val hoursOfSnow: Double,
    @SerializedName("HoursOfIce") val hoursOfIce: Double,
    @SerializedName("CloudCover") val cloudCover: Int,
    @SerializedName("Evapotranspiration") val evapotranspiration: ValueUnit,
    @SerializedName("SolarIrradiance") val solarIrradiance: ValueUnit,
    @SerializedName("RelativeHumidity") val relativeHumidity: RelativeHumidity,
    @SerializedName("WetBulbTemperature") val wetBulbTemperature: Temperature,
    @SerializedName("WetBulbGlobeTemperature") val wetBulbGlobeTemperature: Temperature
)

data class RelativeHumidity(
    @SerializedName("Minimum") val minimum: Int,
    @SerializedName("Maximum") val maximum: Int,
    @SerializedName("Average") val average: Int,
    @SerializedName("Value") val value: Int,
    @SerializedName("Unit") val unit: String,
    @SerializedName("UnitType") val unitType: Int,
    @SerializedName("Phrase") val phrase: String
)

data class Night(
    @SerializedName("Icon") val icon: Int,
    @SerializedName("IconPhrase") val iconPhrase: String,
    @SerializedName("HasPrecipitation") val hasPrecipitation: Boolean,
    @SerializedName("ShortPhrase") val shortPhrase: String,
    @SerializedName("LongPhrase") val longPhrase: String,
    @SerializedName("PrecipitationProbability") val precipitationProbability: Int,
    @SerializedName("ThunderstormProbability") val thunderstormProbability: Int,
    @SerializedName("RainProbability") val rainProbability: Int,
    @SerializedName("SnowProbability") val snowProbability: Int,
    @SerializedName("IceProbability") val iceProbability: Int,
    @SerializedName("Wind") val wind: Wind,
    @SerializedName("Windgust") val windgust : Windgust,
    @SerializedName("TotalLiquid") val totalLiquid: ValueUnit,
    @SerializedName("Rain") val rain: ValueUnit,
    @SerializedName("Snow") val snow: ValueUnit,
    @SerializedName("Ice") val ice: ValueUnit,
    @SerializedName("HoursOfPrecipitation") val hoursOfPrecipitation: Double,
    @SerializedName("HoursOfRain") val hoursOfRain: Double,
    @SerializedName("HoursOfSnow") val hoursOfSnow: Double,
    @SerializedName("HoursOfIce") val hoursOfIce: Double,
    @SerializedName("CloudCover") val cloudCover: Int,
    @SerializedName("Evapotranspiration") val evapotranspiration: ValueUnit,
    @SerializedName("SolarIrradiance") val solarIrradiance: ValueUnit,
    @SerializedName("RelativeHumidity") val relativeHumidity: RelativeHumidity,
    @SerializedName("WetBulbTemperature") val wetBulbTemperature: Temperature,
    @SerializedName("WetBulbGlobeTemperature") val wetBulbGlobeTemperature: Temperature
)

data class AirAndPollen(
    @SerializedName("Name") val name: String,
    @SerializedName("Value") val value: Int,
    @SerializedName("Category") val category: String,
    @SerializedName("CategoryValue") val categoryValue: Int,
    @SerializedName("Type") val type: String
)



data class DegreeDaySummary(
    @SerializedName("Heating") val heating: ValueUnit,
    @SerializedName("Cooling") val cooling: ValueUnit
)

data class DailyForecastResponse(
    @SerializedName("Headline") val headline: Headline,
    @SerializedName("DailyForecasts") val dailyForecasts: List<DailyForecasts>
)
