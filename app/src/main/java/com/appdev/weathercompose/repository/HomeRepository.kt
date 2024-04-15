package com.appdev.weathercompose.repository

import com.appdev.weathercompose.R
import com.appdev.weathercompose.dal.network.AppModule
import com.appdev.weathercompose.dal.network.BaseDataSource
import com.appdev.weathercompose.dal.services.HomeService
import com.appdev.weathercompose.info.generic.Resource
import com.appdev.weathercompose.info.weather.CurrentWeatherInfo
import com.appdev.weathercompose.utils.AppUtils
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val service: HomeService
) : BaseDataSource() {

    suspend fun fetchCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Resource<Response<CurrentWeatherInfo>> {
        if (AppUtils.isInternetAvailable()) {
            val gson = AppModule.provideGson()
            val response = AppModule.provideRetrofit(gson).create(HomeService::class.java)
                .fetchCurrentWeather(latitude, longitude)
            if (response.isSuccessful) {
                return Resource.Success(response, "")
            }
            return Resource.Error(response.message())
        }
        return Resource.Error(AppUtils.getString(R.string.no_internet_connection))
    }
}