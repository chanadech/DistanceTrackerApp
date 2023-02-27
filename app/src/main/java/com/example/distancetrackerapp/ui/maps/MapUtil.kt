package com.example.distancetrackerapp.ui.maps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.text.DecimalFormat

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

    // function to calculate the distance that we have traveled from our location list (which will be updated every time we update our location list)
    fun calculateTheDistance(locationList: MutableList<LatLng>):String{


        if(locationList.size >1 ){                                                                   //check if location list is not empty  -> calculate the distance
            val meters = SphericalUtil.computeDistanceBetween(locationList.first(),locationList.last())   // given start point ([0] or .first()) and end point (last())
            val kilometers = meters / 1000
            return DecimalFormat("#.##").format(kilometers) // two decimal
        }
        else { // location list is not more than one
            return "0.00"
        }

    }

}