package com.raywenderlich.android.runtracker

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build


class PermissionsManager(
        activity: AppCompatActivity,
        private val locationProvider: LocationProvider,
        private val stepCounter: StepCounter // this can be eliminated to stop the step counting-not sure
) {

    //1
    private val locationPermissionProvider = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            locationProvider.getUserLocation()
        }
    }

    private val activityRecognitionPermissionProvider =
            activity.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    stepCounter.setupStepCounter()
                }
            }

    //2
    fun requestUserLocation() {
        locationPermissionProvider.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun requestActivityRecognition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activityRecognitionPermissionProvider.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            stepCounter.setupStepCounter()
        }
    }

}