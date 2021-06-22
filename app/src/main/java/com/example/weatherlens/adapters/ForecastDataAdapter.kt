package com.example.weatherlens.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherlens.R
import com.example.weatherlens.activity.mainActivity.model.ForecastData
import com.example.weatherlens.databinding.ItemForecastBinding

class ForecastDataAdapter(
    private val forecastDataList: ArrayList<ForecastData>,
    private val context: Context
) : RecyclerView.Adapter<ForecastDataAdapter.ForecastDataViewHolder>() {
    inner class ForecastDataViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ForecastData) {
            binding.apply {
                forecastDayTv.text = item.day
                forecastTempTv.text =
                    context.getString(R.string.forecastTempString, item.temperature)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastDataViewHolder {
        val binding =
            ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastDataViewHolder, position: Int) {
        val item = forecastDataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = forecastDataList.size
}