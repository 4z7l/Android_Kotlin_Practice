package com.igluesmik.android_kotlin_practice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SensorService : Service(), SensorEventListener{

    companion object{
        private const val TAG = "SensorService"
        private const val SENSOR_DELAY_TIME = SensorManager.SENSOR_DELAY_NORMAL
        private const val LOCATION_MIN_TIME_MS : Long = 100
        private const val LOCATION_MIN_DISTANCE_M : Float = 1f
    }

    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener

    private lateinit var sensorManager: SensorManager

    private var accelerationSensor : Sensor?= null
    private var magneticSensor : Sensor?= null


    private var accelerationData = FloatArray(3)
    private var magneticData = FloatArray(3)
    private var rotationData = FloatArray(9)
    private var earthData = FloatArray(3)

    private var latitude : Double ?= null
    private var longitude : Double ?= null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")

        initSensor()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")

        detachLocationListener()
        detachSensorListener()
    }

    private fun initSensor() {
        initLocationSensor()
        initMotionSensor()
        attachLocationListener()
        attachSensorListener()
    }

    private fun initLocationSensor() {
        locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location ->
            latitude = location.latitude
            longitude = location.longitude
        }
    }

    private fun initMotionSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.apply {
            accelerationSensor = getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magneticSensor = getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
    }

    private fun attachLocationListener() {

    }

    private fun attachSensorListener() {
        sensorManager.apply {
            registerListener(this@SensorService, accelerationSensor, SENSOR_DELAY_TIME)
            registerListener(this@SensorService, magneticSensor, SENSOR_DELAY_TIME)
        }
    }

    private fun detachLocationListener() {

    }

    private fun detachSensorListener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when(event.sensor.type){
                Sensor.TYPE_MAGNETIC_FIELD  -> getMagneticFieldData(event)
                Sensor.TYPE_ACCELEROMETER   -> getAccelerationData(event)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun getMagneticFieldData(event: SensorEvent) {
        magneticData = event.values.clone()
    }

    private fun getAccelerationData(event: SensorEvent) {
        accelerationData = event.values.clone()
        SensorManager.getRotationMatrix(rotationData, null, accelerationData, magneticData)
        earthData[0] = rotationData[0] * accelerationData[0] + rotationData[1] * accelerationData[1] + rotationData[2] * accelerationData[2]
        earthData[1] = rotationData[3] * accelerationData[0] + rotationData[4] * accelerationData[1] + rotationData[5] * accelerationData[2]
        earthData[2] = rotationData[6] * accelerationData[0] + rotationData[7] * accelerationData[1] + rotationData[8] * accelerationData[2]

        Log.e(TAG, "${earthData[0]}, ${earthData[1]}, ${earthData[2]}")
    }
}