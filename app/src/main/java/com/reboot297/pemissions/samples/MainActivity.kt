package com.reboot297.pemissions.samples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val permissionsObserver = PermissionsObserver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(permissionsObserver)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(permissionsObserver)
        super.onDestroy()
    }
}