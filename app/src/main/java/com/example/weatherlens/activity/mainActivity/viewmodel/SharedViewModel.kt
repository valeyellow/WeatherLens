package com.example.weatherlens.activity.mainActivity.viewmodel

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlens.activity.mainActivity.model.ForecastData
import com.example.weatherlens.activity.mainActivity.model.ForecastResponse
import com.example.weatherlens.activity.mainActivity.model.WeatherResponse
import com.example.weatherlens.network.WeatherAPI
import com.example.weatherlens.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

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

    fun kelvinToCelsius(temp: Double): Int {
        return (temp - 273.15).roundToInt()
    }

    private fun getDayFromTimestamp(timestamp: Int): String =
        DateFormat.format("EEEE", Date(timestamp.toLong() * 1000)).toString()

    fun prepareForecastList(forecastResponse: ForecastResponse? = null): ArrayList<ForecastData> {
        val forecastDataList = ArrayList<ForecastData>()
        val timestampList = mutableListOf<Int>()

        // get current date
        val date = Date()
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val answer: String = formatter.format(date)

        // get timestamp of the current date
        val baseDateTimestamp =
            (SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse("$answer 14:30:00").time / 1000).toInt()

        // create a list of the timestamp of next 4 days at 9:00:00 GMT
        for (i in 1..4) {
            timestampList.add(baseDateTimestamp + (i * 86400))
        }

        // filter the forecast response with data of next 4 days at 9:00:00 GMT
        forecastResponse?.list?.filter { it -> it.dt in timestampList }?.forEach { it ->
            forecastDataList.add(
                ForecastData(
                    day = getDayFromTimestamp(it.dt),
                    temperature = kelvinToCelsius(it.main.temp)
                )
            )
        }
        return forecastDataList
    }

    fun onRetryBtnClick() {
        getWeatherData(queryCity)
        getForecastData(queryCity)
    }
}