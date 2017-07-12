package palarax.arduino_wifi.services

import android.annotation.TargetApi
import android.content.Context
import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.view.InputDevice
import java.util.*

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




@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class InputManagerV16(context: Context) {

    private val mInputManager: InputManager
    private val mListeners: MutableMap<InputManager.InputDeviceListener, V16InputDeviceListener>

    init {
        mInputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
        mListeners = HashMap<InputManager.InputDeviceListener, V16InputDeviceListener>()
    }

    fun getInputDevice(id: Int): InputDevice {
        return mInputManager.getInputDevice(id)
    }

    val inputDeviceIds: IntArray
        get() = mInputManager.inputDeviceIds

    internal class V16InputDeviceListener(idl: InputManager.InputDeviceListener) : InputManager.InputDeviceListener {
        val mIDL: InputManager.InputDeviceListener

        init {
            mIDL = idl
        }

        override fun onInputDeviceAdded(deviceId: Int) {
            mIDL.onInputDeviceAdded(deviceId)
        }

        override fun onInputDeviceChanged(deviceId: Int) {
            mIDL.onInputDeviceChanged(deviceId)
        }

        override fun onInputDeviceRemoved(deviceId: Int) {
            mIDL.onInputDeviceRemoved(deviceId)
        }

    }

    fun registerInputDeviceListener(listener: InputManager.InputDeviceListener, handler: Handler) {
        val v16Listener = V16InputDeviceListener(listener)
        mInputManager.registerInputDeviceListener(v16Listener, handler)
        mListeners.put(listener, v16Listener)
    }

    fun unregisterInputDeviceListener(listener: InputManager.InputDeviceListener) {
        val curListener = mListeners.remove(listener)
        if (null != curListener) {
            mInputManager.unregisterInputDeviceListener(curListener)
        }

    }

}
