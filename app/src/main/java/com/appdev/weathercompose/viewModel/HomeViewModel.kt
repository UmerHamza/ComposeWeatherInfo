package com.appdev.weathercompose.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.weathercompose.info.weather.CurrentWeatherInfo
import com.appdev.weathercompose.info.weather.HomeState
import com.appdev.weathercompose.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _homeState = mutableStateOf(HomeState(false,null,null))
    val weatherHomeState: HomeState
        get() = _homeState.value


    fun fetchCurrentWeather(latitude: Double, longitude: Double) = viewModelScope.launch {
        _homeState.value = HomeState(true,null,null)
        val response = repository.fetchCurrentWeather(latitude, longitude)
        if (response.data != null) {
            _homeState.value = HomeState(false,null,response.data.body())
        } else {
            _homeState.value = HomeState(false,response.message ?: "",null)
        }
    }
}