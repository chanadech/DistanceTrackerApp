package com.example.distancetrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.distancetrackerapp.utils.Permissions


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController


    if(Permissions.hasLocationPermissions(this)){                      // Start at Map fragment when the permission has already been granted (using navigation)
        navController.navigate(R.id.action_permissionFragment_to_mapsFragment)
    }
    }
}