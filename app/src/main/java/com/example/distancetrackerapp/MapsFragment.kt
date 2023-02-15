package com.example.distancetrackerapp

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.distancetrackerapp.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(),OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
       _binding = FragmentMapsBinding.inflate(inflater, container, false)

        binding.startButton.setOnClickListener{

        }
        binding.stopButton.setOnClickListener{

        }
        binding.refreshButton.setOnClickListener {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)      // this from map fragment which implement OnMapReadyCallback -> ทำท่า callback แบบ implement แทน
    }

    override fun onMapReady(p0: GoogleMap) {  // trigger every-time when map ready
//        TODO("Not yet implemented")
    }

    override fun onDestroyView() {  // help to handle memory leak
        super.onDestroyView()
        _binding = null
    }
}