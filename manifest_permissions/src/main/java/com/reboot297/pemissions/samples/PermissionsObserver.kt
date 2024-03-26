/*
 * Copyright (c) 2024. Viktor Pop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.reboot297.pemissions.samples

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar

private const val TAG = "PermissionsObserver"

class PermissionsObserver(
    private val activity: ComponentActivity,
    private var permissions: Array<String> = arrayOf()
) : DefaultLifecycleObserver {

    private val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            val hasDenied = permissions.any { !it.value }
            if (hasDenied) {
                showSnackBar()
            } else {
                // all permissions granted
            }
        }


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initPermissions()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        checkPermissions()
    }

    private fun initPermissions() {
        try {
            permissions = activity.packageManager.getPackageInfo(
                activity.packageName, PackageManager.GET_PERMISSIONS
            ).requestedPermissions
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermissions(): Boolean {
        Log.d(TAG, "available permissions: " + permissions.joinToString(","))

        val requiredPermissions = mutableListOf<String>()
        val warningPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    warningPermissions.add(permission)
                } else {
                    requiredPermissions.add(permission)
                }
            }
        }

        if (requiredPermissions.isNotEmpty()) {
            requestPermissions(requiredPermissions.toTypedArray())
        }

        if (warningPermissions.isNotEmpty()) {
            showRequestPermissionRationale(warningPermissions.toTypedArray())
        }

        return requiredPermissions.isNotEmpty()
    }

    private fun showRequestPermissionRationale(permissions: Array<String>) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.alert_permissions_title)
            .setMessage(R.string.alert_permissions_message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_permissions_ok) { _, _ ->
                requestPermissions(permissions)
            }
            .setNegativeButton(R.string.alert_permissions_cancel, null)
            .show()
    }

    private fun requestPermissions(permissions: Array<String>) {
        requestPermissionLauncher.launch(permissions)
    }

    private fun showSnackBar() {
        Snackbar.make(
            activity.window.decorView.findViewById(android.R.id.content),
            R.string.permissions_denied_message,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.action_settings) {

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.setData(uri)
            activity.startActivity(intent)
        }.show()
    }
}