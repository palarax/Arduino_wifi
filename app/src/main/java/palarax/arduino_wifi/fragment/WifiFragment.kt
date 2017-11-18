package palarax.arduino_wifi.fragment

import android.app.Activity
import android.app.Fragment
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import palarax.arduino_wifi.R
import palarax.arduino_wifi.services.InputManagerCompat
import palarax.arduino_wifi.services.wifiClient


/**
 * Created by Ithai on 28/07/2017.
 */
class WifiFragment : Fragment(), InputManagerCompat.InputDeviceListener {


    val TAG = "WIFI_FRAGMENT"

    lateinit var hostIP: EditText
    lateinit var hostPort: EditText
    lateinit var message: EditText
    lateinit var mainActivity: Activity
    lateinit var replyTextView: TextView


    private var mInputDevice: InputDevice? = null
    lateinit var mControllerManger: InputManagerCompat
    private val DPAD_STATE_LEFT = 1 shl 0
    private val DPAD_STATE_RIGHT = 1 shl 1
    private val DPAD_STATE_UP = 1 shl 2
    private val DPAD_STATE_DOWN = 1 shl 3

    private var mTcpClient: wifiClient? = null


    //attaches an activity
    override fun onAttach(activity: Activity) {
        Log.e(TAG, "onAttach")
        super.onAttach(activity)
        mainActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG, "onCreateView")
        val view = inflater?.inflate(R.layout.wifi_fragment, container, false) as View
        view.setOnKeyListener { v, keyCode, event ->
            onKeyDown(keyCode, event)
        }
        return inflater.inflate(R.layout.wifi_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated")

        hostIP = view.findViewById<EditText>(R.id.edit_ip)
        hostPort = view.findViewById<EditText>(R.id.edit_port)
        message = view.findViewById<EditText>(R.id.edit_message)

        hostIP.setText("192.168.5.1")
        hostPort.setText("22")
        message.setText("TEST_ILYA")

        replyTextView = view.findViewById<TextView>(R.id.textview_reply)

        // connect to the server
        connectTask().execute("")

        val btnSend = view.findViewById<Button>(R.id.btnSendMessage)
        btnSend.setOnClickListener({
            // Handler code here.
            Log.e(TAG, "Send message")

            //sends the message to the server
            if (mTcpClient != null) {
                mTcpClient!!.sendMessage(message.text.toString())
            }

            val info = hostIP.text.toString() + ":" + hostPort.text.toString()
            Toast.makeText(activity, "Sending...$info",
                    Toast.LENGTH_SHORT).show()
        })

        mControllerManger = InputManagerCompat.Factory.getInputManager(activity)
        mControllerManger.registerInputDeviceListener(this, null)
        view.setOnClickListener {
            activity.runOnUiThread({
                Toast.makeText(activity, "test",
                        Toast.LENGTH_SHORT).show()
            })
        }
        view.setOnKeyListener({ v, keyCode, event ->
            activity.runOnUiThread({
                Toast.makeText(activity, keyCode,
                        Toast.LENGTH_SHORT).show()
            })
            onKeyDown(keyCode, event)
            false
        })


    }

    fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val deviceId = event.deviceId
        if (deviceId != -1) {
            onKeyDown(keyCode, event)
        }

        return view.onKeyUp(keyCode, event)
    }

    inner class connectTask : AsyncTask<String, String, wifiClient>() {

        override fun doInBackground(vararg message: String): wifiClient? {

            //we create a TCPClient object and
            mTcpClient = wifiClient(object : wifiClient.OnMessageReceived {
                override //here the messageReceived method is implemented
                fun messageReceived(message: String) {
                    //this method calls the onProgressUpdate
                    publishProgress(message)
                }
            })
            mTcpClient!!.run()

            return null
        }

        override fun onProgressUpdate(vararg values: String) {
            super.onProgressUpdate(*values)

            //in the arrayList we add the messaged received from server
            ///arrayList.add(values[0])
            replyTextView.text = values[0]
            /*activity.runOnUiThread({
                Toast.makeText(activity, values[0],
                        Toast.LENGTH_SHORT).show()

            })*/

            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            //mAdapter.notifyDataSetChanged()
        }
    }

    /*-----------------------Controller Callback ----------------------------------------------*/
    override fun onInputDeviceChanged(deviceId: Int) {
        Toast.makeText(activity, "onInputDeviceChanged",
                Toast.LENGTH_SHORT).show()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        Toast.makeText(activity, "onInputDeviceRemoved",
                Toast.LENGTH_SHORT).show()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        Log.e(TAG, "onInputDeviceAdded")
        mInputDevice = InputDevice.getDevice(deviceId)
        if (null != mInputDevice) {
            var deviceString = "NONE"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                deviceString = mInputDevice!!.descriptor
            } else {
                deviceString = mInputDevice!!.name
            }
            activity.runOnUiThread({
                Toast.makeText(activity, deviceString,
                        Toast.LENGTH_SHORT).show()
            })
        }
    }

    /*override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        val deviceId = event!!.deviceId
        activity.runOnUiThread({
            Toast.makeText(activity, keyCode,
                    Toast.LENGTH_SHORT).show()
        })
        onKeyDown(keyCode,event)
        return true
    }*/

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // Handle DPad keys and fire button on initial down but not on
        // auto-repeat.
        var handled = false
        if (event.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    activity.runOnUiThread({
                        Toast.makeText(activity, "DPAD LEFT",
                                Toast.LENGTH_SHORT).show()
                    })
                    //mDPadState = mDPadState or DPAD_STATE_LEFT
                    handled = true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {

                    activity.runOnUiThread({
                        Toast.makeText(activity, "DPAD RIGHT",
                                Toast.LENGTH_SHORT).show()
                    })
                    //mDPadState = mDPadState or DPAD_STATE_RIGHT
                    handled = true
                }
                KeyEvent.KEYCODE_DPAD_UP -> {

                    activity.runOnUiThread({
                        Toast.makeText(activity, "DPAD UP",
                                Toast.LENGTH_SHORT).show()
                    })
                    //mDPadState = mDPadState or DPAD_STATE_UP
                    handled = true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {

                    activity.runOnUiThread({
                        Toast.makeText(activity, "DPAD DOWN",
                                Toast.LENGTH_SHORT).show()
                    })

                    //mDPadState = mDPadState or DPAD_STATE_DOWN
                    handled = true
                }
                else -> {
                    activity.runOnUiThread({
                        Toast.makeText(activity, "Something else " + keyCode,
                                Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
        return handled
    }


    /* --------------------------Activity SETUP --------------------------------------------*/
    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop")
    }


}