package com.example.distancetrackerapp.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.distancetrackerapp.R
import com.example.distancetrackerapp.databinding.FragmentMapsBinding
import com.example.distancetrackerapp.model.Result
import com.example.distancetrackerapp.service.TrackerService
import com.example.distancetrackerapp.utils.Constants
import com.example.distancetrackerapp.utils.ExtensionFunction.disable
import com.example.distancetrackerapp.utils.ExtensionFunction.enable
import com.example.distancetrackerapp.utils.ExtensionFunction.hide
import com.example.distancetrackerapp.utils.ExtensionFunction.show
import com.example.distancetrackerapp.utils.Permissions
import com.example.distancetrackerapp.utils.requestBackgroundLocationPermission
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MapsFragment : Fragment(), OnMapReadyCallback, OnMyLocationButtonClickListener,EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    val started = MutableLiveData(false)

    private var locationList = mutableListOf<LatLng>()

    // create variable for observe start and stop time from tracker service
    private var startTime = 0L
    private var stopTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        // Add data binding object (already set-up in xml)
        binding.lifecycleOwner = this
        binding.tracking = this // tracking object that create in xml



        binding.startButton.setOnClickListener {
            onStartButtonClick()

        }
        binding.stopButton.setOnClickListener {
            // Enable the stop button when our location list has atleast one lat long object inside
            onStopButtonClick()

        }
        binding.resetButton.setOnClickListener {

        }

        binding.toolsButton.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.action_mapsFragment_to_mapsFragmentWithTools)


        }
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)      // this from map fragment which implement OnMapReadyCallback -> ทำท่า callback แบบ implement แทน
    }

    @SuppressLint("MissingPermission")               // already check -> in permission fragments
    override fun onMapReady(googleMap: GoogleMap) {  // trigger every-time when map ready
        map = googleMap!!
        map.isMyLocationEnabled = true                // display button to navigate to my location
        map.setOnMyLocationButtonClickListener(this)  // ref to our onMyLocationButtonClickListener implement
        map.uiSettings.apply {            // Don't need user to move map by their own => move by our camera view
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isCompassEnabled = false
            isScrollGesturesEnabled = false
        }
        observeTrackerService()
    }


     /** A WAY TO DRAW A PATH- POLYLINE to google map **/
     // Function to observe the same location list from tracker service to fragment -> call when onMapReady
     // Purpose -> whenever the location list from TrackerService receives new values (from updateLocationList() in TrackerService)
     //         ->  our maps fragment will be observe that same locationList
     //             ->  and go to update our new list (locationList that create from mapFragment )
    private fun observeTrackerService(){
        TrackerService.locationList.observe(viewLifecycleOwner) {
            if (it != null) {
                locationList = it                                    // Check locationList isn't null -> update locationList object value that create from mapfragment to locationList that create from tracker service (new list)
                if(locationList.size > 1) {
                    binding.stopButton.enable()                      // Enable stop button -> Check if location list is not empty and only if we receive atleast one latlong object update inside
                }
                Log.d("LocationList", locationList.toString())   // Output from update list = location 1 -> 1,2 -> 1,2,3 -> 1,2,3,..
                drawPolyLine()
                followPolyLine()
            }
        }
        TrackerService.started.observe(viewLifecycleOwner, {
            started.value = it          // set its value to our new mutable data object which we have create inside our maps fragment
        })

         TrackerService.startTime.observe(viewLifecycleOwner,{
             startTime = it             // set start time that create from mapfragment to start time that received from tracker service
         })
         TrackerService.stopTime.observe(viewLifecycleOwner,{
             stopTime = it              // set stop time that create from mapfragment to stop time that received from tracker service
             if(stopTime != 0L){
              showBiggerPicture()                          // zoom-out and create camera animation to show to user, the whole path that user travelled
              displayResult()                              // navigate to result fragment with the actual result

             }
         })

     }



    private fun drawPolyLine(){
        val polyLine = map.addPolyline(
            PolylineOptions().apply {
                width(10f)
                color(Color.BLUE)
                jointType(JointType.ROUND)
                startCap(ButtCap())
                endCap(ButtCap())
                addAll(locationList)



            }
        )
    }


    // function to set Camera position every time we receive a new location update -> camera will follow the user location
    // camera position -> we will choose our last item inside the locationList
    //                 -> last item in this location list is always a new position on the map
    private fun followPolyLine(){
        if(locationList.isNotEmpty()){
            map.animateCamera(
                (CameraUpdateFactory.newCameraPosition(
                    MapUtil.setCamaraPosition(
                        locationList.last()
                    )
                )),
                1000,
                null)
        }
    }


    private fun onStartButtonClick() {
        if(Permissions.hasBackgroundLocationPermissions(requireContext(),)){                // Check the background location permissions when user tab start button if true -> Log
            Log.d("MapsActivity","Background Permission already enabled")
            startCountDown()                                                                // START BUTTON CLICK THEN START COUNTDOWN
            binding.startButton.disable()
            binding.startButton.hide()
            binding.stopButton.show()

        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    private fun onStopButtonClick() {
         stopForegroundService()
        binding.stopButton.hide()
        binding.startButton.show()

    }



    private fun startCountDown() {
        binding.timerTextView.show()
        binding.stopButton.disable()                                                                   // we dont want user to cancel anything when count down timer is start, when the count down timer is timer is finished and we recerive a final location from our service -> enable stop button
        val timer: CountDownTimer = object : CountDownTimer(4000, 1000) {   // start time (4),(1) =>  3 ,2 ,1, 0
            override fun onTick(millisUntilFinished: Long) {                                           // trigger in every second
                val currentSecond = millisUntilFinished / 1000                                         // (millisUntilFinished / one second) = currentSecond on every tick in our countdown timer ; current second = 3 -> 2 -> 1 -> 0
                if (currentSecond.toString() == "0"){
                    binding.timerTextView.text = "GO"
                    binding.timerTextView.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.black
                    ))
                }
                else {
                    binding.timerTextView.text = currentSecond.toString()
                    binding.timerTextView.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.red
                    ))
                }
            }

            override fun onFinish() {                                        // trigger when count down timer is complete
                sendActionCommandToService(Constants.ACTION_SERVICE_START)   // SEND COMMAND TO TRACKERSERVICE AFTER COUNTDOWN FINISH
                binding.timerTextView.hide()
            }
        }
        timer.start()
    }

    private fun stopForegroundService() {
        binding.startButton.disable()
        sendActionCommandToService(Constants.ACTION_SERVICE_STOP)
    }

    private fun sendActionCommandToService(action:String) {           // this function will send a specific action that give as parameter to our "TrackerService" and after that we want to start our service
                                                                      // should be call on onFinish -> request Service after  countdown onFinish() success
        Intent(
            requireContext(),
            TrackerService::class.java
        ).apply{
            this.action = action
            requireContext().startService(this) // this = intent
        }
    }

    private fun showBiggerPicture() {                               // trigger every time when stop-time value changes and stop-time value not a zero when we actually stop the foreground service
        val bounds = LatLngBounds.Builder()
        for(location in locationList){                              // want to include the each location from the list inside our latlng bounds object
            bounds.include(location)                                //location = latlng object
        }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(
            bounds.build(), 100
        ), 2000, null)

    }

    private fun displayResult(){
        val result = Result(
            MapUtil.calculateTheDistance(locationList),
            MapUtil.calculateElapsedTime(startTime, stopTime)
        )
        lifecycleScope.launch{
            delay(2500)
            val directions = MapsFragmentDirections.actionMapsFragmentToResultFragment(result)         //MapsFragmentDirections generate when we add argument to the navigation graph -> use to send the argument to other class from maps fragment -> don't forget to rebuild project
            findNavController().navigate(directions)
            binding.startButton.apply {
                hide()
                enable()
            }
            binding.stopButton.hide()
            binding.resetButton.show()
        }

    }


    override fun onMyLocationButtonClick(): Boolean {                                 // handle how myLocation button clicked
        binding.hintTextView.animate().alpha(0f).duration = 1500                // animate textview when press our location button -> alpha value -> 1 show, 0 gone
        lifecycleScope.launch{
            delay(2500)
            binding.hintTextView.hide()
            binding.startButton.show()
            binding.toolsButton.hide()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(requireActivity()).build().show()        // User denied permission will show this dialog
        }
        else {
            requestBackgroundLocationPermission(this)
        }
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        onStartButtonClick()
    }

    override fun onDestroyView() {  // help to handle memory leak
        super.onDestroyView()
        _binding = null
    }
}