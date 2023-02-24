package com.example.distancetrackerapp.ui.maps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

object MapUtil {
    fun setCamaraPosition(location: LatLng): CameraPosition {
        return CameraPosition.Builder()
            .target(location)
            .zoom(18f)
            .build()
    }

    // Function to calculate the elapsed time
    // use when  we start sending results to our results fragment
    fun calculateElapsedTime(startTime: Long, stopTime: Long): String {
        val elapsedTime = stopTime - startTime

        val seconds = (elapsedTime / 1000).toInt() % 60
        val minutes = (elapsedTime / (1000 * 60) % 60)
        val hours = (elapsedTime / (1000 * 60 * 60) % 24)

        return "$hours:$minutes:$seconds"
    }

}