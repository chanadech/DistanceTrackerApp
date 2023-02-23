package com.example.distancetrackerapp.ui.maps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

object MapUtil {
    fun setCamaraPosition(location: LatLng): CameraPosition{
        return CameraPosition.Builder()
            .target(location)
            .zoom(18f)
            .build()
    }
}