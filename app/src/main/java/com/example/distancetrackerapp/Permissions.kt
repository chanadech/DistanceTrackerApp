package com.example.distancetrackerapp

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.example.distancetrackerapp.Constants.PERMISSIONS_LOCATION_REQUEST_CODE
import pub.devrel.easypermissions.EasyPermissions

// USE EasyPrmissions library -> help when working with runtime permissions
object Permissions {

// check location permissions
    fun hasLocationPermissions(context: Context) =   // return bool true if have permission else false
    EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )




// request location permission
    fun requestLocationPermissions(fragment: Fragment){
        EasyPermissions.requestPermissions(
            fragment,
            "This App cant't work without Location permissions",
            PERMISSIONS_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION

        )
    }
}