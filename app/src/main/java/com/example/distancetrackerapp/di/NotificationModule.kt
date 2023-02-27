package com.example.distancetrackerapp.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.distancetrackerapp.ui.MainActivity
import com.example.distancetrackerapp.R
import com.example.distancetrackerapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped  // scope to tracker service
    @Provides       // use for the class that we dont own to actually tell our hilt lib how to provide them or the instance of those class
    fun providePendingIntent(
        @ApplicationContext context: Context)
    : PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getActivity(
                context,
                Constants.PENDING_INTENT_REQUEST_CODE,
                Intent(context, MainActivity::class.java)                             // PENDING INTENT DEFINE WHERE WE WANT TO NAVIGATE WHEN WE PRESS OUR NOTIFICATION
                // .apply { this.action = Constants.ACTION_NAVIGATE_TO_MAP_FRAGMENT } // check by navigation in MainActivity instead of this
                ,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT   //FLAG_MUTABLE -> API >= 31
            )

        }
        else {
            PendingIntent.getActivity(
                context,
                Constants.PENDING_INTENT_REQUEST_CODE,
                Intent(context, MainActivity::class.java)                             // PENDING INTENT DEFINE WHERE WE WANT TO NAVIGATE WHEN WE PRESS OUR NOTIFICATION
                ,
                PendingIntent.FLAG_UPDATE_CURRENT   //FLAG_MUTABLE -> API >= 31
            )

        }

    }


    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(                                              // function to describe how to provide notification
        @ApplicationContext context:Context,
        pendingIntent: PendingIntent
    ):NotificationCompat.Builder{
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)  // ยกเลิก autocancel อัตโนมัติไม่ได้
            .setOngoing(true)      //  ทำให้การแจ้งเตือนคงที่และยกเลิกไม่ได้ -> หยุดเมื่อ stop service\
            .setSmallIcon(R.drawable.ic_run_24)
            .setContentIntent(pendingIntent)                                     // launch mapfragment from our notification -> tap noti then map appears -> create function to show how to provide the pending intent
    }

    // tell dagger lib how to provide notification manager
    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}


// Define How to provide  NotificationManager, NotificationCompat.Builder,  PendingIntent -> Inject ได้