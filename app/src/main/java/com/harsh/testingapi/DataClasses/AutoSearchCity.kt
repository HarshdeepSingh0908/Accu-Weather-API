package com.harsh.testingapi.DataClasses


import com.google.gson.annotations.SerializedName

data class AutoSearchCity(
    @SerializedName("Version") val version: Int,
    @SerializedName("Key") val key: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Rank") val rank: Int,
    @SerializedName("LocalizedName") val localizedName: String,
    @SerializedName("Country") val country: Country,
    @SerializedName("AdministrativeArea") val administrativeArea: AdministrativeArea
){
    override fun toString(): String {
        return  "$localizedName | ${country.localizedName}"
    }
}

data class Country(
    @SerializedName("ID") val id: String,
    @SerializedName("LocalizedName") val localizedName: String
)

data class AdministrativeArea(
    @SerializedName("ID") val id: String,
    @SerializedName("LocalizedName") val localizedName: String
)