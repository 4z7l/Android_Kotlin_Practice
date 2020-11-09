package com.igluesmik.android_kotlin_practice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = Intent(this, SensorService::class.java)

        toggleBtn.setOnClickListener {
            if (toggleBtn.isChecked) {
                startService(service)
            } else {
                stopService(service)
            }
        }

    }


}