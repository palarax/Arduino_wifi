package palarax.arduino_wifi

import android.app.Activity
import android.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import palarax.arduino_wifi.services.wifiClient


/**
 * Created by Ithai on 28/07/2017.
 */
class WifiFragment : Fragment() {

    val TAG = "WIFI_FRAGMENT"

    lateinit var hostIP: EditText
    lateinit var hostPort: EditText
    lateinit var message: EditText
    lateinit var mainActivity: Activity
    lateinit var replyTextView: TextView

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
        return inflater?.inflate(R.layout.wifi_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated")

        hostIP = view.findViewById(R.id.edit_ip) as EditText
        hostPort = view.findViewById(R.id.edit_port) as EditText
        message = view.findViewById(R.id.edit_message) as EditText

        hostIP.setText("192.168.5.1")
        hostPort.setText("22")
        message.setText("TEST_ILYA")

        replyTextView = view.findViewById(R.id.textview_reply) as TextView

        // connect to the server
        connectTask().execute("")

        val btnSend = view.findViewById(R.id.btnSendMessage) as Button
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