package com.example.distancetrackerapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.distancetrackerapp.R
import com.example.distancetrackerapp.databinding.FragmentMapsWithToolsBinding
import com.example.distancetrackerapp.utils.TypeAndStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.navigationrail.NavigationRailView

class MapsWithToolsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap                              // 1. initial map variable ก่อน เพื้่อเอาไปใช้ใน  onMapReady function
    private lateinit var binding: FragmentMapsWithToolsBinding
    private val typeAndStyle by lazy { TypeAndStyle() }

    private val ati = LatLng(14.084037703890884, 100.42053077551579)

    private val markerPositions = mutableListOf<LatLng>()
    private val markerObjects = mutableListOf<Marker>()
    private var centerMarker: Marker? = null
    private var polygon: Polygon? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsWithToolsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        val rail: NavigationRailView = binding.missionRail

        binding.missionRail.setOnClickListener{
            Toast.makeText(
                requireContext(),
                "Clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.missionRail.menu.findItem(R.id.mission_tab_field).setOnMenuItemClickListener {
            false }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        typeAndStyle.setMapStyle(map, requireContext())

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ati, 30f))

        map.uiSettings.apply {
            isZoomControlsEnabled = true        // ทำให้มีปุ่ม +- สำหรับ zoomเข้าออก เพิ่มขึ้นมาในหน้าแอป
            isCompassEnabled = true             // ทำให้มีเข็มทิศโผล่มุมซ้ายเวลากด rotate ถ้ากดเข็มทิศมันก็จะพากลับมาที่ default location ละก็ค่อยๆหายไป อยาก disable ก็แค่ set fault
            isMyLocationButtonEnabled = true    // ทำให้มาโลเคชั่นของเครื่องที่ใช้อยู่โดยตรง -> ใช้ได้ต่อเมื่อ myLocation layer enable นะ -> เดี่ยวทำทีหลัง
        }

        onMapClick(true)
        setupCenterMarkerDragListener()

    }

    private fun setupCenterMarkerDragListener() {
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                // Do nothing
            }

            override fun onMarkerDrag(marker: Marker) {
                // Do nothing
            }

            override fun onMarkerDragEnd(marker: Marker) {
                if (marker == centerMarker) {
                    val oldCenter = calculatePolygonCenter(markerPositions)
                    val newCenter = marker.position
                    val dx = newCenter.latitude - oldCenter.latitude
                    val dy = newCenter.longitude - oldCenter.longitude

                    // Remove the old markers
                    for (markerObject in markerObjects) {
                        markerObject.remove()
                    }
                    markerObjects.clear()

                    // Update marker positions and add new markers
                    for (i in markerPositions.indices) {
                        val oldPos = markerPositions[i]
                        val newPos = LatLng(oldPos.latitude + dx, oldPos.longitude + dy)
                        markerPositions[i] = newPos
                        val newMarker = map.addMarker(MarkerOptions().position(newPos))
                        newMarker?.let { markerObjects.add(it) }
                    }

                    createPolygon()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_types_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        typeAndStyle.setMapType(item, map)
        return true
    }

    private fun onMapClick(enablePolygon: Boolean) {
        when (enablePolygon) {
            true -> map.setOnMapClickListener { latlng ->
                Toast.makeText(
                    requireContext(),
                    "Latitude: ${latlng.latitude}\n Longitude: ${latlng.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
                val marker = map.addMarker(MarkerOptions().position(latlng))
                marker?.let { markerObjects.add(it) }
                markerPositions.add(latlng)
                createPolygon()
            }
            else -> {Log.i("MapsWithToolsFragment", "Map polygon not available")}
        }

    }
    private fun createPolygon() {
        polygon?.remove()
        val polygonOptions = PolygonOptions().apply {
            addAll(markerPositions)
            strokeColor(R.color.orange)
        }
        polygon = map.addPolygon(polygonOptions)
        updateCenterMarker()
    }

    private fun updateCenterMarker() {
        centerMarker?.remove()
        val center = calculatePolygonCenter(markerPositions)
        centerMarker = map.addMarker(
            MarkerOptions().position(center).title("Center")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        centerMarker?.isDraggable = true
    }

    private fun calculatePolygonCenter(points: List<LatLng>): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        val totalPoints = points.size

        for (point in points) {
            latitude += point.latitude
            longitude += point.longitude
        }

        return LatLng(latitude / totalPoints, longitude / totalPoints)
    }


}