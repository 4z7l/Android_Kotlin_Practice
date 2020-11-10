package com.igluesmik.android_kotlin_practice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this
                , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        }

        val service = Intent(this, SensorService::class.java)

        toggleBtn.setOnClickListener {
            if (toggleBtn.isChecked) {
                actionOnService(Actions.START)
                //startService(service)
            } else {
                actionOnService(Actions.STOP)
            }
        }

    }

    private fun actionOnService(action : Actions){
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, SensorService::class.java).also {
            it.action = action.name
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }


}