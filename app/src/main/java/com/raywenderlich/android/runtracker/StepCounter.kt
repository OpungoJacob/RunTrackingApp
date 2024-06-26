package com.raywenderlich.android.runtracker

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData

class StepCounter(private val activity: AppCompatActivity) :
        SensorEventListener {
            

    val liveSteps = MutableLiveData<Int>()

    private val sensorManager by lazy {
        activity.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private var initialSteps = -1

    fun setupStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        event.values.firstOrNull()?.toInt()?.let { newSteps ->
            //1
            if (initialSteps == -1) {
                initialSteps = newSteps
            }

            //2
            val currentSteps = newSteps - initialSteps

            //3
            liveSteps.value = currentSteps
        }
    }

    fun unloadStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}