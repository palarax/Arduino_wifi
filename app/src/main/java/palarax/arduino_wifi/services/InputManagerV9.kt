package palarax.arduino_wifi.services

import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.util.SparseArray
import android.view.InputDevice
import android.view.MotionEvent
import palarax.arduino_wifi.services.InputManagerCompat.InputDeviceListener
import java.lang.ref.WeakReference
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


class InputManagerV9 : InputManagerCompat {


    companion object {
        private val LOG_TAG = "InputManagerV9"
        private val MESSAGE_TEST_FOR_DISCONNECT = 101
        private val CHECK_ELAPSED_TIME = 3000L

        private val ON_DEVICE_ADDED = 0
        private val ON_DEVICE_CHANGED = 1
        private val ON_DEVICE_REMOVED = 2
    }

    private val mDevices: SparseArray<LongArray>
    private val mListeners: MutableMap<InputDeviceListener, Handler>
    private val mDefaultHandler: Handler

    private class PollingMessageHandler internal constructor(im: InputManagerV9) : Handler() {
        private val mInputManager: WeakReference<InputManagerV9>

        init {
            mInputManager = WeakReference(im)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_TEST_FOR_DISCONNECT -> {
                    val imv = mInputManager.get()
                    if (null != imv) {
                        val time = SystemClock.elapsedRealtime()
                        val size = imv.mDevices.size()
                        for (i in 0..size - 1) {
                            val lastContact = imv.mDevices.valueAt(i)
                            if (null != lastContact) {
                                if (time - lastContact[0] > CHECK_ELAPSED_TIME) {
                                    // check to see if the device has been
                                    // disconnected
                                    val id = imv.mDevices.keyAt(i)
                                    if (null == InputDevice.getDevice(id)) {
                                        // disconnected!
                                        imv.notifyListeners(ON_DEVICE_REMOVED, id)
                                        imv.mDevices.remove(id)
                                    } else {
                                        lastContact[0] = time
                                    }
                                }
                            }
                        }
                        sendEmptyMessageDelayed(MESSAGE_TEST_FOR_DISCONNECT,
                                CHECK_ELAPSED_TIME)
                    }
                }
            }
        }

    }

    init {
        mDevices = SparseArray<LongArray>()
        mListeners = HashMap<InputDeviceListener, Handler>()
        mDefaultHandler = PollingMessageHandler(this)
        // as a side-effect, populates our collection of watched
        // input devices
        inputDeviceIds
    }

    override fun getInputDevice(id: Int): InputDevice {
        return InputDevice.getDevice(id)
    }

    // add any hitherto unknown devices to our
    // collection of watched input devices
    // we have a new device
    override val inputDeviceIds: IntArray
        get() {
            val activeDevices = InputDevice.getDeviceIds()
            val time = SystemClock.elapsedRealtime()
            for (id in activeDevices) {
                val lastContact = mDevices.get(id)
                if (null == lastContact) {
                    mDevices.put(id, longArrayOf(time))
                }
            }
            return activeDevices
        }

    override fun unregisterInputDeviceListener(listener: InputDeviceListener) {
        mListeners.remove(listener)
    }

    override fun registerInputDeviceListener(listener: InputDeviceListener, handler: Handler?) {
        mListeners.remove(listener)
        //if (handler == null) handler = mDefaultHandler
        mListeners.put(listener, handler as Handler)
    }

    private fun notifyListeners(why: Int, deviceId: Int) {
        // the state of some device has changed
        if (!mListeners.isEmpty()) {
            // yes... this will cause an object to get created... hopefully
            // it won't happen very often
            for (listener in mListeners.keys) {
                val handler = mListeners[listener]
                val odc = DeviceEvent.getDeviceEvent(why, deviceId, listener)
                handler?.post(odc)
            }
        }
    }

    private class DeviceEvent private constructor() : Runnable {
        private var mMessageType: Int = 0
        private var mId: Int = 0
        private var mListener: InputDeviceListener? = null

        override fun run() {
            when (mMessageType) {
                ON_DEVICE_ADDED -> mListener!!.onInputDeviceAdded(mId)
                ON_DEVICE_CHANGED -> mListener!!.onInputDeviceChanged(mId)
                ON_DEVICE_REMOVED -> mListener!!.onInputDeviceRemoved(mId)
                else -> Log.e(LOG_TAG, "Unknown Message Type")
            }
            // dump this runnable back in the queue
            sEventQueue.offer(this)
        }

        companion object {
            private val sEventQueue = ArrayDeque<DeviceEvent>()

            internal fun getDeviceEvent(messageType: Int, id: Int,
                                        listener: InputDeviceListener): DeviceEvent {
                var curChanged: DeviceEvent? = sEventQueue.poll()
                if (null == curChanged) {
                    curChanged = DeviceEvent()
                }
                curChanged.mMessageType = messageType
                curChanged.mId = id
                curChanged.mListener = listener
                return curChanged
            }
        }
    }

    override fun onGenericMotionEvent(event: MotionEvent) {
        // detect new devices
        val id = event.deviceId
        var timeArray: LongArray? = mDevices.get(id)
        if (null == timeArray) {
            notifyListeners(ON_DEVICE_ADDED, id)
            timeArray = LongArray(1)
            mDevices.put(id, timeArray)
        }
        val time = SystemClock.elapsedRealtime()
        timeArray[0] = time
    }

    override fun onPause() {
        mDefaultHandler.removeMessages(MESSAGE_TEST_FOR_DISCONNECT)
    }

    override fun onResume() {
        mDefaultHandler.sendEmptyMessage(MESSAGE_TEST_FOR_DISCONNECT)
    }


}
