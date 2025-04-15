package com.myapp
//
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var initialStep = -1
    private var latestStep = 0

    companion object {
        const val ACTION_STEP_UPDATE = "com.aatif.STEP_UPDATE"
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        Log.e("SesnorActive","true")
        startForegroundNotification()
    }

    private fun startForegroundNotification() {
        val channelId = "step_channel"
        val channelName = "Step Counter"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(chan)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Step Counter Running")
            .setContentText("Tracking your steps")
            .setSmallIcon(R.drawable.rn_edit_text_material) // add icon
            .build()

        startForeground(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            val prefs = getSharedPreferences("step_prefs", MODE_PRIVATE)

            var initialStep = prefs.getInt("initial_step", -1)
            if (initialStep == -1) {
                initialStep = totalSteps
                prefs.edit().putInt("initial_step", initialStep).apply()
            }

            val stepsToday = totalSteps - initialStep

            val intent = Intent(ACTION_STEP_UPDATE)
            intent.putExtra("steps", stepsToday)
            Log.w("BroadCastSteps",stepsToday.toString())
            sendBroadcast(intent)

            prefs.edit().putInt("steps", stepsToday).apply()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        super.onDestroy()
        Log.e("StoppedService","True")
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
