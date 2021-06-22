package com.example.weatherlens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.weatherlens.activity.mainActivity.view.ForecastFragment
import com.example.weatherlens.activity.mainActivity.viewmodel.SharedViewModel
import com.example.weatherlens.databinding.ActivityMainBinding
import com.example.weatherlens.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            retryBtn.setOnClickListener {
                viewModel.onRetryBtnClick()
            }
        }


        viewModel.weatherData.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoader(binding)
                }
                is Resource.Success -> {
                    val tempInCelsius = response.data?.main?.temp?.let {
                        viewModel.kelvinToCelsius(
                            it
                        )
                    }

                    val formattedString = tempInCelsius.toString() + "\u00B0"

                    binding.apply {
                        temperatureTv.text = formattedString
                    }
                    hideLoader(binding, isSuccess = true)
                }
                is Resource.Error -> {
                    hideLoader(binding, isSuccess = false)
                }
            }
        }

        viewModel.forecastData.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoader(binding)
                }
                is Resource.Success -> {
                    val forecastDataList = response.data?.let { viewModel.prepareForecastList(it) }
                    Log.d(TAG, "onCreate message: $forecastDataList")

                    val forecastFragment =
                        forecastDataList?.let { ForecastFragment.newInstance(it) }

                    if (forecastFragment != null) {
                        supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                            .replace(
                                R.id.fragment_container,
                                forecastFragment,
                                "forecastFrag"
                            )
                            .commit()
                    }

                    hideLoader(binding, isSuccess = true)
                }
                is Resource.Error -> {
                    hideLoader(binding, isSuccess = false)
                }

            }
        }
    }

    private fun hideLoader(binding: ActivityMainBinding, isSuccess: Boolean) {
        binding.apply {
            loadingIndicator.visibility = View.INVISIBLE
            if (isSuccess) {
                currentWeatherLayout.visibility = View.VISIBLE
                requestFailureLayout.visibility = View.INVISIBLE
            } else {
                currentWeatherLayout.visibility = View.INVISIBLE
                requestFailureLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoader(binding: ActivityMainBinding) {
        binding.apply {
            currentWeatherLayout.visibility = View.INVISIBLE
            requestFailureLayout.visibility = View.INVISIBLE
            loadingIndicator.visibility = View.VISIBLE
        }
    }
}