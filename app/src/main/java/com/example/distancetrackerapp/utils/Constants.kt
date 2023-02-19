package com.example.distancetrackerapp.utils

object Constants {

    const val PERMISSIONS_LOCATION_REQUEST_CODE = 1
    const val PERMISSION_BACKGROUD_LOCATION_REQUEST_CODE = 2


    // use for sent command from mapfragment to trigger service
    const  val ACTION_SERVICE_START = "ACTION_SERVICE_START"
    const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"
    const val ACTION_NAVIGATE_TO_MAP_FRAGMENT = "ACTION_NAVIGATE_TO_MAP_FRAGMENT "

    // For di foreground sending
    const val NOTIFICATION_CHANNEL_ID = "tracker_notification_id"
    const val NOTIFICATION_CHANNEL_NAME = "tracker_notification_name"
    const val NOTIFICATION_ID = 3

    // FOR di pending
    const val PENDING_INTENT_REQUEST_CODE = 99




}