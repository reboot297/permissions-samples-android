/*
 * Copyright (c) 2024. Viktor Pop
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.reboot297.permission.samples.basic

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class FirstFragment : Fragment() {

    private val permissionsObserver = PermissionsObserver(
        this,
        initialPermissions = arrayOf(
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.POST_NOTIFICATIONS,
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(permissionsObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_first, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.askPermissionButton).setOnClickListener {
            permissionsObserver.checkPermissions(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.READ_SMS
                )
            )
        }

        view.findViewById<View>(R.id.nextScreenButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.app_content, SecondFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setTitle(R.string.first_fragment_label)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(permissionsObserver)
    }
}