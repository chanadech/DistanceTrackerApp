package com.example.distancetrackerapp.utils

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.example.distancetrackerapp.utils.Constants.PERMISSIONS_LOCATION_REQUEST_CODE
import com.example.distancetrackerapp.utils.Constants.PERMISSION_BACKGROUD_LOCATION_REQUEST_CODE
import pub.devrel.easypermissions.EasyPermissions

// USE EasyPrmissions library -> help when working with runtime permissions
object Permissions {

    // check location permissions
    fun hasLocationPermissions(context: Context) =
        // return bool true if have permission else false
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


    // request location permission
    fun requestLocationPermissions(fragment: Fragment) {
        EasyPermissions.requestPermissions(
            fragment,
            "This App cant't work without Location permissions",
            PERMISSIONS_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION

        )
    }

    // Allow Background Permission -> For API > 29 will use this function to check the permission  (API < 29 will auto handle)
    //  Create two function which is checking and requesting background location permission
    fun hasBackgroundLocationPermissions(context: Context): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){     // Q = api 29 (API < 29 return true )
            return EasyPermissions.hasPermissions(              // API = 29 or higher will return check permissions
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return true
    }
}
// request background location permission
fun requestBackgroundLocationPermission(fragment: Fragment){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        return EasyPermissions.requestPermissions(
            fragment,
            "Background location permission is essential to this application without it we will not to be able to provide you with our service", // if user denied permission show this
            PERMISSION_BACKGROUD_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }
}