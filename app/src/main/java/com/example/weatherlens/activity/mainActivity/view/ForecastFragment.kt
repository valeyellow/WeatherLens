package com.example.weatherlens.activity.mainActivity.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.weatherlens.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment: Fragment(R.layout.fragment_forecast) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}