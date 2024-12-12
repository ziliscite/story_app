package com.submission.storyapp.presentation.core.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.submission.storyapp.R
import com.submission.storyapp.databinding.ActivityMapsBinding
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel: MapsViewModel by viewModels()
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()

        val dicodingSpace = LatLng(0.7893, 113.9213)

        mMap.apply {
            uiSettings.isTiltGesturesEnabled = true
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = true

            addMarker(MarkerOptions().position(dicodingSpace).title("Dicoding Space").snippet("Batik Kumeli No.50"))
            animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 0f))
        }

        viewModel.stories.observe(this) { stories ->
            when (stories) {
                is ResponseWrapper.Success -> addMarkers(stories.data)
                is ResponseWrapper.Error -> showToast(stories.error)
                ResponseWrapper.Loading -> {}
            }
        }
    }

    private fun addMarkers(stories: List<Story>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat.toDouble(), story.lon.toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(story.description))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                40
            )
        )
    }

    private fun setMapStyle() {
        try {
            if (!mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))) {
                showToast("Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            showToast("Can't find style: ${exception.message}")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}