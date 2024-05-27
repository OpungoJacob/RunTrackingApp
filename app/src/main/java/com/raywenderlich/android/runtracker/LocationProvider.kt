@file:Suppress("DEPRECATION")

package com.raywenderlich.android.runtracker

//imports
//import androidx.annotation.SuppressLint
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.maps.android.SphericalUtil
import kotlin.math.roundToInt

@SuppressLint("MissingPermission")
class LocationProvider(private val activity: AppCompatActivity) {

    private val client by lazy { LocationServices.getFusedLocationProviderClient(activity) }

    private val locations = mutableListOf<LatLng>()
    private var distance = 0

    val liveLocations = MutableLiveData<List<LatLng>>()
    val liveDistance = MutableLiveData<Int>()
    val liveLocation = MutableLiveData<LatLng>()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {

            //1
//        private val client
//                by lazy { LocationServices.getFusedLocationProviderClient(activity) }
            val currentLocation = result.lastLocation
            //val latLng = currentLocation?.let { LatLng(it.latitude, currentLocation.longitude) }
//           val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)  expected code
            val latLng = LatLng(currentLocation!!.latitude, currentLocation.longitude)

            //2
//        private val locations = mutableListOf<LatLng>()
            val lastLocation = locations.lastOrNull()

            //3
//        val liveLocation = MutableLiveData<LatLng>()
            if (lastLocation != null) {
                distance +=SphericalUtil.computeDistanceBetween(lastLocation, latLng).roundToInt()
                liveDistance.value = distance
            }





            //4
//    fun getUserLocation() {
//        client.lastLocation.addOnSuccessListener { location ->
//            val latLng = LatLng(location.latitude, location.longitude)
//            locations.add(latLng)
//            liveLocation.value = latLng
//        }
//    }

            locations.add(latLng)
            liveLocations.value = locations

        }
    }

    fun trackUser() {
        //1
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000

        //2
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopTracking() {
        client.removeLocationUpdates(locationCallback)
        locations.clear()
        distance = 0
    }

    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                locations.add(latLng)
                liveLocation.value = latLng
            } else {
                Log.e("LocationProvider", "Location is null")
            }
        }
    }
}
