package com.igluesmik.android_kotlin_practice

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.util.*
import android.os.PowerManager as PowerManager

//https://robertohuertas.com/2019/06/29/android_foreground_services/
class SensorService : Service(), SensorEventListener, LocationListener {

    private var wakeLock: PowerManager.WakeLock ?= null
    private var isServiceStarted = false

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager

    private var accelerationSensor: Sensor? = null
    private var magneticSensor: Sensor? = null

    private var accelerationData = FloatArray(3)
    private var magneticData = FloatArray(3)
    private var rotationData = FloatArray(9)
    private var earthData = FloatArray(3)

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var timer = Timer()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")

        if(intent != null){
            when(intent.action){
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
            }
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
        initSensor()

        val notification = createNotification()
        startForeground(1, notification)

    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG, "onBind")

        return null
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")

        detachLocationListener()
        detachSensorListener()
    }

    private fun startService() {
        if(isServiceStarted) return
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)


        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessSensorService::lock").apply {
                acquire()
            }
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                insertData()
            }
        }, 0, 10000)

    }

    private fun stopService() {
        try {
            wakeLock?.let {
                if(it.isHeld){
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
            timer.cancel()
        }catch (e: Exception){
            Log.e("SEULGI", ""+e.message)
        }

        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun initSensor() {
        initLocationSensor()
        initMotionSensor()
        attachLocationListener()
        attachSensorListener()
    }

    private fun initLocationSensor() {
        locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
    }

    private fun initMotionSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.apply {
            accelerationSensor = getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magneticSensor = getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
    }

    @SuppressLint("MissingPermission")
    private fun attachLocationListener() {
        locationManager.apply {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_MIN_TIME_MS,
                LOCATION_MIN_DISTANCE_M,
                this@SensorService
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_MIN_TIME_MS,
                LOCATION_MIN_DISTANCE_M,
                this@SensorService
            )
        }
    }

    private fun attachSensorListener() {
        sensorManager.apply {
            registerListener(this@SensorService, accelerationSensor, SENSOR_DELAY_TIME)
            registerListener(this@SensorService, magneticSensor, SENSOR_DELAY_TIME)
        }
    }

    private fun detachLocationListener() {
        locationManager.removeUpdates(this)
    }

    private fun detachSensorListener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> getMagneticFieldData(event)
                Sensor.TYPE_ACCELEROMETER -> getAccelerationData(event)
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
        earthData[0] =
            rotationData[0] * accelerationData[0] + rotationData[1] * accelerationData[1] + rotationData[2] * accelerationData[2]
        earthData[1] =
            rotationData[3] * accelerationData[0] + rotationData[4] * accelerationData[1] + rotationData[5] * accelerationData[2]
        earthData[2] =
            rotationData[6] * accelerationData[0] + rotationData[7] * accelerationData[1] + rotationData[8] * accelerationData[2]

    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
    }

    private fun createNotification() : Notification{
        val notificationChannelId = "Sensor Service Channel"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(notificationChannelId, "Sensor Service Channel", NotificationManager.IMPORTANCE_HIGH)

            channel.apply {
                description = "Notification description"
                enableLights(true)
                lightColor = Color.YELLOW
            }
            notificationManager.createNotificationChannel(channel)
        }
        val pendingIntent : PendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        val builder
                = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder(this, notificationChannelId)
        } else {
            Notification.Builder(this)
        }


        return builder
            .setContentTitle("Content Title")
            .setContentText("Content Text")
            .setContentIntent(pendingIntent)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()

    }

    private fun insertData(){
        Log.e("SEULGI","lat = ${latitude}, lon = ${longitude}")
    }

    companion object{
        private const val TAG = "SensorService"
        private const val SENSOR_DELAY_TIME = SensorManager.SENSOR_DELAY_NORMAL
        private const val LOCATION_MIN_TIME_MS : Long = 100
        private const val LOCATION_MIN_DISTANCE_M : Float = 1f
    }
}