package com.reboot297.pemissions.samples

import android.R
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


class PermissionsObserver(
    private val activity: ComponentActivity,
    // TODO add map of rationale Messages, if empty = use default message
    private var permissions: Array<String> = arrayOf()
) : DefaultLifecycleObserver {

    private val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            permissions.forEach {
                // TODO(Check all permissions)
                if (it.value) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        }


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        try {
            permissions = activity.packageManager.getPackageInfo(
                activity.packageName, PackageManager.GET_PERMISSIONS
            ).requestedPermissions
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        checkManifestPermissions()
    }

    fun checkManifestPermissions(): Boolean {
        permissions.forEach {
            Log.d("TAG", "checkManifestPermissions: " + it)
        }

        val notGrantedPermissions: MutableList<String> = ArrayList()
        var needsMessage = false
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notGrantedPermissions.add(permission)
                needsMessage = (needsMessage || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                ))
            }
        }

        if (notGrantedPermissions.size > 0) {
            requestPermissions(notGrantedPermissions.toTypedArray(), needsMessage)
        }

        return notGrantedPermissions.size == 0
    }

    fun requestPermissions(permissions: Array<String>, needsMessage: Boolean) {
        if (needsMessage) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            //builder.setTitle(R.string.alert_permissions_title)
            //builder.setMessage(R.string.alert_permissions_message)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.ok) { _, _ ->
                requestPermissionLauncher.launch(permissions)
            }
            builder.show()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }
}