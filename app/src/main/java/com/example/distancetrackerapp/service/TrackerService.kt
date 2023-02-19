package com.example.distancetrackerapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.distancetrackerapp.utils.Constants
import com.example.distancetrackerapp.utils.Constants.ACTION_SERVICE_START
import com.example.distancetrackerapp.utils.Constants.ACTION_SERVICE_STOP
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    companion object {
        val started = MutableLiveData<Boolean>()
    }

    private fun setInitialValues(){
        started.postValue(false)
    }

    override fun onCreate() {
        setInitialValues()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {  // when call sendActionCommandToService() in MapsFragment this function will automatically be triggered from our TraggerService

        // Check action that request is equal to ACTION_START_SERVICE or ACTION_STOP_SERVICE
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    started.postValue(true)
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