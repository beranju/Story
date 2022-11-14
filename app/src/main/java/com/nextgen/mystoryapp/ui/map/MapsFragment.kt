package com.nextgen.mystoryapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nextgen.mystoryapp.R
import com.nextgen.mystoryapp.data.story.remote.dto.StoryResponse
import com.nextgen.mystoryapp.domain.story.entity.StoryEntity
import com.nextgen.mystoryapp.ui.common.extention.showAlertDialog
import com.nextgen.mystoryapp.ui.common.extention.showToast
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import java.io.IOException
import java.util.*


@WithFragmentBindings
@AndroidEntryPoint
class MapsFragment : Fragment() {
    private val viewModel: MapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private val boundsBuilder = LatLngBounds.builder()
    private var dataStory: StoryEntity? = null

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle(googleMap)
        getMyLocation(googleMap)
    }

    private fun getStoryDetail(mMap: GoogleMap, data: StoryEntity) {
        lat = data.lat?.toDouble()!!
        lon = data.lon?.toDouble()!!
        val storyLocation = LatLng(lat, lon)
        val addressName = getAddressName(lat, lon)

        mMap.addMarker(MarkerOptions()
            .position(storyLocation)
            .title("${data.name}")
            .snippet(addressName)
            .icon(vectorToBitmap(R.drawable.custom_marker)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storyLocation, 17F))
    }

    private fun vectorToBitmap(@DrawableRes customMarker: Int): BitmapDescriptor? {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, customMarker, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isgranted: Boolean ->
        if (isgranted) {
            getMyLocation(googleMap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        dataStory = arguments?.getParcelable("storyEntity")

        getAllStory()
        observer()

    }

    private fun observer() {
        viewModel.mState.observe(viewLifecycleOwner) { result ->
            handleState(result)
        }
    }

    private fun handleState(state: MapState?) {
        when (state) {
            is MapState.Init -> Unit
            is MapState.ShowToast -> context?.showToast(state.message)
            is MapState.Success -> handleSuccess(state.storyEntity)
            is MapState.Error -> handleError(state.rawResponse)
            else -> {}
        }
    }

    private fun handleError(rawResponse: StoryResponse) {
        context?.showAlertDialog(rawResponse.message.toString())
    }

    private fun handleSuccess(storyEntity: StoryResponse) {
        if (dataStory != null) {
            getStoryDetail(googleMap, dataStory!!)
        } else {

            storyEntity.listStory.forEach { data ->
                lat = data.lat?.toDouble()!!
                lon = data.lon?.toDouble()!!
                val latLng = LatLng(lat, lon)
                val addressName = getAddressName(lat, lon)
                googleMap.addMarker(MarkerOptions()
                    .position(latLng)
                    .title(data.name)
                    .snippet(addressName)
                    .icon(vectorToBitmap(R.drawable.custom_marker)))
                boundsBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    500
                )
            )
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val list = geoCoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun getAllStory() {
        viewModel.getStoriesLocation(1)
    }


    private fun getMyLocation(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        this.googleMap = googleMap
        try {
            val success =
                this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.requireContext(),
                    R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Cant't find style. Error : ", e)
        }
    }


    companion object {
        private val TAG = "MapsFragment"
    }
}