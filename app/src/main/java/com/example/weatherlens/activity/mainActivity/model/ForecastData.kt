package com.example.weatherlens.activity.mainActivity.model

import android.os.Parcel
import android.os.Parcelable

data class ForecastData(
    val day: String,
    val temperature: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(day)
        parcel.writeInt(temperature)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ForecastData> {
        override fun createFromParcel(parcel: Parcel): ForecastData {
            return ForecastData(parcel)
        }

        override fun newArray(size: Int): Array<ForecastData?> {
            return arrayOfNulls(size)
        }
    }
}