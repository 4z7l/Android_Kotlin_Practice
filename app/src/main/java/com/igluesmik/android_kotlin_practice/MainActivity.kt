package com.igluesmik.android_kotlin_practice

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var accelerationSensor : Sensor?= null
    private var magneticSensor : Sensor?= null

    private var accelerationData = FloatArray(3)
    private var magneticData = FloatArray(3)
    private var earthData = FloatArray(3)
    private var rotationMatrix = FloatArray(9)

    private var info : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMotionSensor()
    }

    override fun onResume() {
        super.onResume()

        sensorManager.apply {
            registerListener(this@MainActivity, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL)
            registerListener(this@MainActivity, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

    private fun initMotionSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.apply {
            accelerationSensor = getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magneticSensor = getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when(event.sensor.type){
                Sensor.TYPE_ACCELEROMETER   -> getAccelerationData(event)
                Sensor.TYPE_MAGNETIC_FIELD  -> getMagneticFieldData(event)
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

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerationData, magneticData)

        ////get acceleration values converted to a world coordinate system
        earthData[0] = rotationMatrix[0] * accelerationData[0] + rotationMatrix[1] * accelerationData[1] + rotationMatrix[2] * accelerationData[2]
        earthData[1] = rotationMatrix[3] * accelerationData[0] + rotationMatrix[4] * accelerationData[1] + rotationMatrix[5] * accelerationData[2]
        earthData[2] = rotationMatrix[6] * accelerationData[0] + rotationMatrix[7] * accelerationData[1] + rotationMatrix[8] * accelerationData[2]

        info = "Device Coordinate\n"+
                "x = ${accelerationData[0]},  y = ${accelerationData[1]}, z = ${accelerationData[2]}"
        acceDeviceCoordinate.text = info

        info = "Device Coordinate\n"+
                "x = ${earthData[0]},  y = ${earthData[1]}, z = ${earthData[2]}"
        acceWorldCoordinate.text = info
    }

}