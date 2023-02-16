package com.example.distancetrackerapp

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.distancetrackerapp.databinding.FragmentMapsBinding
import com.example.distancetrackerapp.utils.ExtensionFunction.hide
import com.example.distancetrackerapp.utils.ExtensionFunction.show

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback, OnMyLocationButtonClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)



        binding.startButton.setOnClickListener {

        }
        binding.stopButton.setOnClickListener {

        }
        binding.refreshButton.setOnClickListener {

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

    }


    override fun onMyLocationButtonClick(): Boolean {                           // handle how myLocation button clicked
        binding.hintTextView.animate().alpha(0f).duration = 1500                // animate textview when press our location button -> alpha value -> 1 show, 0 gone
        lifecycleScope.launch{
            delay(2500)
            binding.hintTextView.hide()
            binding.startButton.show()
        }
        return false
    }

    override fun onDestroyView() {  // help to handle memory leak
        super.onDestroyView()
        _binding = null
    }
}