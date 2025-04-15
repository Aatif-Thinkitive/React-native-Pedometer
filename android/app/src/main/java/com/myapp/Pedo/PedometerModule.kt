package com.myapp

import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class PedometerModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val MODULE_NAME = "Pedometer"
    }

    private val prefs: SharedPreferences by lazy {
        reactContext.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
    }

    override fun getName(): String = MODULE_NAME

    @ReactMethod
    fun startStepService() {
        val serviceIntent = Intent(reactContext, StepCounterService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            reactContext.startForegroundService(serviceIntent)
        } else {
            reactContext.startService(serviceIntent)
        }
    }

    @ReactMethod
    fun stopStepService() {
        val serviceIntent = Intent(reactContext, StepCounterService::class.java)
        reactContext.stopService(serviceIntent)

        prefs.edit().putInt("steps", 0).putInt("initial_step", -1).apply()
    }

    @ReactMethod
    fun getCurrentStepData(promise: Promise) {
        val steps = prefs.getInt("steps", 0)
        val result = Arguments.createMap().apply {
            putInt("steps", steps)
        }
        promise.resolve(result)
    }
}
