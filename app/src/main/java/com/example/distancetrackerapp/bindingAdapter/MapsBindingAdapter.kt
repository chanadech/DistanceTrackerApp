package com.example.distancetrackerapp.bindingAdapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter

class MapsBindingAdapter { // whenever data object changes its value -> this custom binding adapter will be triggered -> follow the logic below

    companion object {
        // Create function which (add  2 parameter -> 1. Actual view on which we are going to use the binding adapter -> View (TextView or etc) , 2. Boolean value
        @BindingAdapter("observeTracking")
        @JvmStatic
        fun observeTracking(view:View, started:Boolean){ // check started value = true/false and view is button -> set it visible
                                                         //                                      view is TextView -> set it invisible
            if (started && view is Button){              // close app and click noti -> open app again -> show stop button
                view.visibility = View.VISIBLE
            } else if (started && view is TextView){
                view.visibility = View.INVISIBLE
            }
        }
    }
}