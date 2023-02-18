package com.example.distancetrackerapp.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.distancetrackerapp.utils.Constants.ACTION_SERVICE_START
import com.example.distancetrackerapp.utils.Constants.ACTION_SERVICE_STOP

class TrackerService : LifecycleService() {

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
}