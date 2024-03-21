package com.harsh.testingapi.DataClasses


import com.google.gson.annotations.SerializedName

data class NightDataClass(
    @SerializedName("CloudCover")
    var cloudCover: Int? = null, // 10
    @SerializedName("Evapotranspiration")
    var evapotranspiration: Evapotranspiration? = null,
    @SerializedName("HasPrecipitation")
    var hasPrecipitation: Boolean? = null, // false
    @SerializedName("HoursOfIce")
    var hoursOfIce: Int? = null, // 0
    @SerializedName("HoursOfPrecipitation")
    var hoursOfPrecipitation: Int? = null, // 0
    @SerializedName("HoursOfRain")
    var hoursOfRain: Int? = null, // 0
    @SerializedName("HoursOfSnow")
    var hoursOfSnow: Int? = null, // 0
    @SerializedName("Ice")
    var ice: Ice? = null,
    @SerializedName("IceProbability")
    var iceProbability: Int? = null, // 0
    @SerializedName("Icon")
    var icon: Int? = null, // 35
    @SerializedName("IconPhrase")
    var iconPhrase: String? = null, // Partly cloudy
    @SerializedName("LongPhrase")
    var longPhrase: String? = null, // Partly cloudy
    @SerializedName("PrecipitationProbability")
    var precipitationProbability: Int? = null, // 1
    @SerializedName("Rain")
    var rain: Rain? = null,
    @SerializedName("RainProbability")
    var rainProbability: Int? = null, // 1
    @SerializedName("RelativeHumidity")
    var relativeHumidity: RelativeHumidity? = null,
    @SerializedName("ShortPhrase")
    var shortPhrase: String? = null, // Partly cloudy
    @SerializedName("Snow")
    var snow: Snow? = null,
    @SerializedName("SnowProbability")
    var snowProbability: Int? = null, // 0
    @SerializedName("SolarIrradiance")
    var solarIrradiance: SolarIrradiance? = null,
    @SerializedName("ThunderstormProbability")
    var thunderstormProbability: Int? = null, // 0
    @SerializedName("TotalLiquid")
    var totalLiquid: TotalLiquid? = null,
    @SerializedName("WetBulbGlobeTemperature")
    var wetBulbGlobeTemperature: WetBulbGlobeTemperature? = null,
    @SerializedName("WetBulbTemperature")
    var wetBulbTemperature: WetBulbTemperature? = null,
    @SerializedName("Wind")
    var wind: Wind? = null,
    @SerializedName("WindGust")
    var windGust: WindGust? = null
) {
    data class Evapotranspiration(
        @SerializedName("Unit")
        var unit: String? = null, // in
        @SerializedName("UnitType")
        var unitType: Int? = null, // 1
        @SerializedName("Value")
        var value: Double? = null // 0.01
    )

    data class Ice(
        @SerializedName("Unit")
        var unit: String? = null, // in
        @SerializedName("UnitType")
        var unitType: Int? = null, // 1
        @SerializedName("Value")
        var value: Int? = null // 0
    )

    data class Rain(
        @SerializedName("Unit")
        var unit: String? = null, // in
        @SerializedName("UnitType")
        var unitType: Int? = null, // 1
        @SerializedName("Value")
        var value: Int? = null // 0
    )

    data class RelativeHumidity(
        @SerializedName("Average")
        var average: Int? = null, // 53
        @SerializedName("Maximum")
        var maximum: Int? = null, // 64
        @SerializedName("Minimum")
        var minimum: Int? = null // 34
    )

    data class Snow(
        @SerializedName("Unit")
        var unit: String? = null, // in
        @SerializedName("UnitType")
        var unitType: Int? = null, // 1
        @SerializedName("Value")
        var value: Int? = null // 0
    )

    data class SolarIrradiance(
        @SerializedName("Unit")
        var unit: String? = null, // W/m²
        @SerializedName("UnitType")
        var unitType: Int? = null, // 33
        @SerializedName("Value")
        var value: Int? = null // 0
    )

    data class TotalLiquid(
        @SerializedName("Unit")
        var unit: String? = null, // in
        @SerializedName("UnitType")
        var unitType: Int? = null, // 1
        @SerializedName("Value")
        var value: Int? = null // 0
    )

    data class WetBulbGlobeTemperature(
        @SerializedName("Average")
        var average: Average? = null,
        @SerializedName("Maximum")
        var maximum: Maximum? = null,
        @SerializedName("Minimum")
        var minimum: Minimum? = null
    ) {
        data class Average(
            @SerializedName("Unit")
            var unit: String? = null, // F
            @SerializedName("UnitType")
            var unitType: Int? = null, // 18
            @SerializedName("Value")
            var value: Int? = null // 59
        )

        data class Maximum(
            @SerializedName("Unit")
            var unit: String? = null, // F
            @SerializedName("UnitType")
            var unitType: Int? = null, // 18
            @SerializedName("Value")
            var value: Int? = null // 67
        )

        data class Minimum(
            @SerializedName("Unit")
            var unit: String? = null, // F
            @SerializedName("UnitType")
            var unitType: Int? = null, // 18
            @SerializedName("Value")
            var value: Int? = null // 53
        )
    }

    data class WetBulbTemperature(
        @SerializedName("Average")
        var average: Average? = null,
        @SerializedName("Maximum")
        var maximum: Maximum? = null,
        @SerializedName("Minimum")
        var minimum: Minimum? = null
    ) {
        data class Average(
            @SerializedName("Unit")
            var unit: String? = null, // F
            @SerializedName("UnitType")
            var unitType: Int? = null, // 18
            @SerializedName("Value")
            var value: Int? = null // 52
        )

        data class Maximum(
            @SerializedName("Unit")
            var unit: String? = null, // F
            @SerializedName("UnitType")
            var unitType: Int? = null, // 18
            @SerializedName("Value")
            var value: Int? = null // 57
        )

        data class Minimum(
            @SerializedName("Unit")
            var unit: String? = null, // F
            @SerializedName("UnitType")
            var unitType: Int? = null, // 18
            @SerializedName("Value")
            var value: Int? = null // 49
        )
    }

    data class Wind(
        @SerializedName("Direction")
        var direction: Direction? = null,
        @SerializedName("Speed")
        var speed: Speed? = null
    ) {
        data class Direction(
            @SerializedName("Degrees")
            var degrees: Int? = null, // 310
            @SerializedName("English")
            var english: String? = null, // NW
            @SerializedName("Localized")
            var localized: String? = null // NW
        )

        data class Speed(
            @SerializedName("Unit")
            var unit: String? = null, // mi/h
            @SerializedName("UnitType")
            var unitType: Int? = null, // 9
            @SerializedName("Value")
            var value: Double? = null // 3.5
        )
    }

    data class WindGust(
        @SerializedName("Direction")
        var direction: Direction? = null,
        @SerializedName("Speed")
        var speed: Speed? = null
    ) {
        data class Direction(
            @SerializedName("Degrees")
            var degrees: Int? = null, // 300
            @SerializedName("English")
            var english: String? = null, // WNW
            @SerializedName("Localized")
            var localized: String? = null // WNW
        )

        data class Speed(
            @SerializedName("Unit")
            var unit: String? = null, // mi/h
            @SerializedName("UnitType")
            var unitType: Int? = null, // 9
            @SerializedName("Value")
            var value: Double? = null // 10.4
        )
    }
}