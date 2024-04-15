package com.appdev.weathercompose.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.appdev.weathercompose.BuildConfig
import com.appdev.weathercompose.application.WeatherApp.Companion.appContext
import com.appdev.weathercompose.info.weather.Weather


object AppUtils {

    fun convertKelvinToCelsius(temperature:Double?): String {
        if(temperature == null){
            return ""
        }
        return ("${String.format("%.0f",(temperature - 273.15))} \u2103")
    }

    fun fetchImageUrl(weatherList: List<Weather?>?):String{
        if(weatherList.isNullOrEmpty()){
            return BuildConfig.ICON_BASE_URL
        }
        val iconName = weatherList[0]?.icon
        val link =  "${BuildConfig.ICON_BASE_URL}$iconName.png"
        return link
    }

    fun fetchWeatherDescription(weatherList:List<Weather?>?): String {
        if(weatherList.isNullOrEmpty()){
            return ""
        }
        return weatherList[0]?.description ?: ""
    }

    fun checkIfPermissionGranted(context: Context, permission: String): Boolean {
        return (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun getString(@StringRes stringResId: Int): String {
        return appContext?.getString(stringResId) ?: ""
    }

    fun openPermissionSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }

    fun showToast(message: String?) {
        if (message.isNullOrEmpty() || appContext == null) {
            return
        }
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities)
                ?: return false

        val result: Boolean = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }
}