/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.trip_to_hyeonchungsa.tthLib.rendering

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.Surface
import android.view.WindowManager
import com.google.ar.core.Session

/**
 * Helper to track the display rotations. In particular, the 180 degree rotations are not notified
 * by the onSurfaceChanged() callback, and thus must be tracked manually.
 */
class DisplayRotationHelper(private val context: Context) :
  android.hardware.display.DisplayManager.DisplayListener {
  private var viewportChanged = false
  private var viewportWidth = 0
  private var viewportHeight = 0
  private val displayManager =
    context.getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
  private val display
    get() = displayManager.getDisplay(android.view.Display.DEFAULT_DISPLAY)
      ?: throw RuntimeException("Unable to get default display")

  /**
   * Registers the display listener. Should be called from [Activity.onPause].
   */
  fun onResume() {
    displayManager.registerDisplayListener(this, null)
  }

  /**
   * Unregisters the display listener. Should be called from [Activity.onPause].
   */
  fun onPause() {
    displayManager.unregisterDisplayListener(this)
  }

  /**
   * Records a change in surface dimensions. This will be later used by [ ]. Should be called from [ ].
   */
  fun onSurfaceChanged(width: Int, height: Int) {
    viewportWidth = width
    viewportHeight = height
    viewportChanged = true
  }

  /**
   * Updates the session display geometry if a change was posted either by [ ] or by [.onDisplayChanged].
   * This function should be called constantly during the render loop.
   */
  fun updateSessionIfNeeded(session: Session) {
    if (viewportChanged) {
      val displayRotation = display.rotation
      session.setDisplayGeometry(displayRotation, viewportWidth, viewportHeight)
      viewportChanged = false
    }
  }

  /**
   * Returns the aspect ratio of the GL surface viewport while accounting for the display rotation
   * relative to the device camera sensor orientation.
   */
  fun getCameraSensorRelativeViewportAspectRatio(cameraId: String): Float {
    val cameraSensorToDisplayRotation = getCameraSensorToDisplayRotation(cameraId)
    return when (cameraSensorToDisplayRotation) {
      90,
      270 -> viewportHeight.toFloat() / viewportWidth.toFloat()
      0,
      180 -> viewportWidth.toFloat() / viewportHeight.toFloat()
      else -> throw RuntimeException("Unhandled rotation: $cameraSensorToDisplayRotation")
    }
  }

  /**
   * Returns the rotation of the back-facing camera sensor relative to the display. Same as [ ].
   */
  fun getCameraSensorToDisplayRotation(cameraId: String): Int {
    val characteristics =
      try {
        (context.getSystemService(Context.CAMERA_SERVICE) as CameraManager).getCameraCharacteristics(
          cameraId
        )
      } catch (e: CameraAccessException) {
        throw RuntimeException("Unable to determine display orientation", e)
      }

    // Camera sensor orientation is 90 degrees for most devices.
    val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
    val displayRotation = display.rotation * 90

    // Swaps the camera sensor orientation if the device is a tablet, since tablets are landscape
    // by default.
    return (sensorOrientation - displayRotation + 360) % 360
  }

  override fun onDisplayAdded(displayId: Int) {}
  override fun onDisplayRemoved(displayId: Int) {}
  override fun onDisplayChanged(displayId: Int) {
    viewportChanged = true
  }
}
