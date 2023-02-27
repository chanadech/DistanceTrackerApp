package com.example.distancetrackerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// use this class to send the data using a safe args from our map fragment to result fragment -> add Parcelable

@Parcelize
data class Result(
    var distance: String,
    var time: String
) : Parcelable
