package com.harsh.testingapi
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
        @Query("q") query: String
    ): Response<LocationSearchResponse>

    @GET("forecasts/v1/daily/1day/{locationKey}")
    suspend fun getDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String
    ): Response<DailyForecastResponse>
}