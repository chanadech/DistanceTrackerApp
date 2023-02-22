package com.example.distancetrackerapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.distancetrackerapp.utils.Constants
import com.example.distancetrackerapp.utils.Constants.ACTION_SERVICE_START
import com.example.distancetrackerapp.utils.Constants.ACTION_SERVICE_STOP
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val started = MutableLiveData<Boolean>()
    }

    private val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(result: LocationResult) { // will be called every few seconds  when we receive a new location update from our user
            super.onLocationResult(result)
            result?.locations?.let { locations ->
                for(location in locations) {
                    val newLatLng = LatLng(location.latitude, location.longitude) // receive new location from user will print new latlng
                    Log.d("TrackerService", newLatLng.toString())
                }

            }
        }

    }

    private fun setInitialValues(){
        started.postValue(false)
    }

    override fun onCreate() {
        setInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {  // when call sendActionCommandToService() in MapsFragment this function will automatically be triggered from our TraggerService

        // Check action that request is equal to ACTION_START_SERVICE or ACTION_STOP_SERVICE
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    started.postValue(true)
                    startForegroundService()        // RECEIVED COMMAND THAT SEND FROM TRACKERSERVICE WILL START FOREGROUNDSERVICE
                    startLocationUpdates()
                }

                ACTION_SERVICE_STOP -> {
                    started.postValue(false)

                }

                else -> {}
            }
        }
        return super.onStartCommand(
            intent,
            flags,
            startId
        )
    }

    private fun startForegroundService(){ // call after send action
        createNotificationChannel()       // check api >= 26
        startForeground(Constants.NOTIFICATION_ID, notification.build()) // OPEN A NEW NOTIFICATION
    }

    // function get location update in every 2-3 sec
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        val locationRequest  = LocationRequest().apply { // สร้าง object สำหรับ ขอตำแหน่งแบบพื้นฐานแล้ว
            interval  = Constants.LOCATION_UPDATE_INTERVAL                //ช่วงเวลาจริงที่เราต้องการอัพเดทตำแหน่ง
            fastestInterval = Constants.LOCATION_FASTEST_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY            // ต้องการความแม่นยำส๎งเพื่อติดตาม user
        }
        fusedLocationProviderClient.requestLocationUpdates( // ปกติต้องเช็ค permission -> แต่อันนี้เราได้รับอยู่แล้วเลยใช้ SuppressLint เลย
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }



    //function to create notification channel
    fun createNotificationChannel(){ // if we use > API 26 (0) -> can create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel) // NotificationChannel will create when we use sdk version >= 26 if not will not need notification channel
        }
    }
}