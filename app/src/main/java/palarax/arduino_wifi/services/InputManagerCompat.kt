package palarax.arduino_wifi.services

import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.InputDevice
import android.view.MotionEvent

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


interface InputManagerCompat {
    /**
     * Gets information about the input device with the specified id.

     * @param id The device id
     * *
     * @return The input device or null if not found
     */
    fun getInputDevice(id: Int): InputDevice

    /**
     * Gets the ids of all input devices in the system.

     * @return The input device ids.
     */
    val inputDeviceIds: IntArray

    /**
     * Registers an input device listener to receive notifications about when
     * input devices are added, removed or changed.

     * @param listener The listener to register.
     * *
     * @param handler The handler on which the listener should be invoked, or
     * *            null if the listener should be invoked on the calling thread's
     * *            looper.
     */
    fun registerInputDeviceListener(listener: InputDeviceListener,
                                    handler: Handler?)

    /**
     * Unregisters an input device listener.

     * @param listener The listener to unregister.
     */
    fun unregisterInputDeviceListener(listener: InputManagerCompat.InputDeviceListener)

    /*
     * The following three calls are to simulate V16 behavior on pre-Jellybean
     * devices. If you don't call them, your callback will never be called
     * pre-API 16.
     */

    /**
     * Pass the motion events to the InputManagerCompat. This is used to
     * optimize for polling for controllers. If you do not pass these events in,
     * polling will cause regular object creation.

     * @param event the motion event from the app
     */
    fun onGenericMotionEvent(event: MotionEvent)

    /**
     * Tell the V9 input manager that it should stop polling for disconnected
     * devices. You can call this during onPause in your activity, although you
     * might want to call it whenever your game is not active (or whenever you
     * don't care about being notified of new input devices)
     */
    fun onPause()

    /**
     * Tell the V9 input manager that it should start polling for disconnected
     * devices. You can call this during onResume in your activity, although you
     * might want to call it less often (only when the gameplay is actually
     * active)
     */
    fun onResume()

    interface InputDeviceListener {
        /**
         * Called whenever the input manager detects that a device has been
         * added. This will only be called in the V9 version when a motion event
         * is detected.

         * @param deviceId The id of the input device that was added.
         */
        fun onInputDeviceAdded(deviceId: Int)

        /**
         * Called whenever the properties of an input device have changed since
         * they were last queried. This will not be called for the V9 version of
         * the API.

         * @param deviceId The id of the input device that changed.
         */
        fun onInputDeviceChanged(deviceId: Int)

        /**
         * Called whenever the input manager detects that a device has been
         * removed. For the V9 version, this can take some time depending on the
         * poll rate.

         * @param deviceId The id of the input device that was removed.
         */
        fun onInputDeviceRemoved(deviceId: Int)
    }

    /**
     * Use this to construct a compatible InputManager.
     */
    object Factory {

        /**
         * Constructs and returns a compatible InputManger

         * @param context the Context that will be used to get the system
         * *            service from
         * *
         * @return a compatible implementation of InputManager
         */
        fun getInputManager(context: Context): InputManagerCompat {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return InputManagerV16(context)
            } else {
                return InputManagerV9()
            }
        }
    }
}
