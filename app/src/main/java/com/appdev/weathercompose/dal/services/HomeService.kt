package com.appdev.weathercompose.dal.services

import com.appdev.weathercompose.BuildConfig
import com.appdev.weathercompose.constants.ApiUrls
import com.appdev.weathercompose.info.weather.CurrentWeatherInfo
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    @GET(ApiUrls.FETCH_CURRENT_WEATHER)
    suspend fun fetchCurrentWeather(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("appid") api_key: String = BuildConfig.API_KEY
    ): Response<CurrentWeatherInfo>

}

