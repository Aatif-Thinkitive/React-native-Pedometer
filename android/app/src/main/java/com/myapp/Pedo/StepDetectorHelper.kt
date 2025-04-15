package com.myapp


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class StepCounterHelper(private val context: Context) : SensorEventListener {


    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var initialStep: Int = -1
    private var latestStep: Int = 0

    init {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
//            Log.e("EventValues",event.values[0].toString())
            val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

            var initialStep = prefs.getInt("initial_step", -1)
            if (initialStep == -1) {
                initialStep = totalSteps
                prefs.edit().putInt("initial_step", initialStep).apply()
            }

            latestStep = totalSteps - initialStep
            Log.w("LatestSteps",latestStep.toString())
        }
    }


    fun getCurrentSteps(): Int = latestStep

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}