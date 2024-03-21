package com.harsh.testingapi
import com.harsh.testingapi.DataClasses.AutoSearchCity
import com.harsh.testingapi.DataClasses.DailyForecastResponse
import com.harsh.testingapi.DataClasses.LocationSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
interface WeatherApiService {
    @GET("locations/v1/cities/geoposition/search")
    suspend fun searchLocation(
        @Query("apikey") apiKey: String,
        @Query("q") query: String,
        @Query("details") details: Boolean
    ): Response<LocationSearchResponse>

    @GET("forecasts/v1/daily/1day/{locationKey}")
    suspend fun getDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("details") details: Boolean
    ): Response<DailyForecastResponse>
    @GET("forecasts/v1/daily/5day/{locationKey}")
    suspend fun get5DayDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("details") details: Boolean,
        @Query("language") language: String,
        @Query("metric") metric: Boolean
    ): Response<DailyForecastResponse>
    @GET("locations/v1/cities/autocomplete")
    suspend fun autocompleteCities(
        @Query("apikey") apiKey: String,
        @Query("q") query: String,
        @Query("language") language: String = "en-us"
    ): Response<List<AutoSearchCity>>

}