package com.example.weatherlens.activity.mainActivity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlens.activity.mainActivity.model.ForecastResponse
import com.example.weatherlens.activity.mainActivity.model.WeatherResponse
import com.example.weatherlens.network.WeatherAPI
import com.example.weatherlens.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

private fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this
const val queryCity = "Bengaluru"

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val api: WeatherAPI
) : ViewModel() {
    private val _weatherData = MutableLiveData<Resource<WeatherResponse>>()
    private val _forecastData = MutableLiveData<Resource<ForecastResponse>>()

    val weatherData = _weatherData.asLiveData()
    val forecastData = _forecastData.asLiveData()

    init {
        getWeatherData(queryCity)
        getForecastData(queryCity)
    }

    private fun getForecastData(queryCity: String) = viewModelScope.launch {
        _forecastData.postValue(Resource.Loading())
        try {
            val response = api.getForecast(queryCity)
            _forecastData.postValue(handleForecastResponse(response))

        } catch (e: Exception) {
            when (e) {
                is IOException -> _forecastData.postValue(Resource.Error(message = "Some error occurred ${e.message}!"))
                else -> _forecastData.postValue(Resource.Error(message = e.message))
            }
        }

    }

    private fun handleForecastResponse(response: Response<ForecastResponse>): Resource<ForecastResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(data = it)
            }
        }
        return Resource.Error(message = response.message())
    }

    private fun getWeatherData(queryCity: String) = viewModelScope.launch {
        _weatherData.postValue(Resource.Loading())
        try {
            val response = api.getWeather(queryCity)
            _weatherData.postValue(handleWeatherResponse(response))
        } catch (e: Exception) {
            when (e) {
                is IOException -> _weatherData.postValue(Resource.Error(message = "Some error occurred!"))
                else -> _weatherData.postValue(Resource.Error(message = "Conversion Error"))
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>): Resource<WeatherResponse>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(data = it)
            }
        }
        return Resource.Error(message = response.message())
    }

}