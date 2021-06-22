package com.example.weatherlens.activity.mainActivity.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.weatherlens.R
import com.example.weatherlens.activity.mainActivity.model.ForecastData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private lateinit var forecastData: ArrayList<ForecastData>

    companion object {
        @JvmStatic
        fun newInstance(data: ArrayList<ForecastData>) = ForecastFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("forecastDataList", data)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelableArrayList<ForecastData>("forecastDataList")?.let {
            forecastData = it
        }
    }
}