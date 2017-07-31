package palarax.arduino_wifi.services

import android.util.Log
import java.io.*
import java.net.InetAddress
import java.net.Socket


/**
 * Created by Ithai on 28/07/2017.
 */

class wifiClient
/**
 * Constructor of the class. OnMessagedReceived listens for the messages received from server
 */
(listener: OnMessageReceived) {

    private var serverMessage: String? = null
    private var mMessageListener: OnMessageReceived? = null
    private var mRun = false

    internal var out: PrintWriter? = null
    internal var out2: DataOutputStream? = null
    internal var bufferin: BufferedReader? = null

    init {
        mMessageListener = listener
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    fun sendMessage(message: String) {

        if (out != null && !out!!.checkError()) {
            val thread = Thread(Runnable {
                try {
                    //Your code goes here

                    out!!.println(message)
                    out!!.flush()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()
        }

    }

    fun stopClient() {
        mRun = false
    }

    fun run() {

        mRun = true

        try {
            //here you must put your computer's IP address.
            val serverAddr = InetAddress.getByName(SERVERIP)

            Log.e("TCP Client", "C: Connecting...")

            //create a socket to make the connection with the server
            val socket = Socket(serverAddr, SERVERPORT)

            try {

                //send the message to the server
                out = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)

                Log.e("TCP Client", "C: Sent.")

                Log.e("TCP Client", "C: Done.")

                //receive the message which the server sends back
                bufferin = BufferedReader(InputStreamReader(socket.getInputStream()))

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = bufferin!!.readLine()

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener!!.messageReceived(serverMessage!!)
                    }
                    serverMessage = null

                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '$serverMessage'")

            } catch (e: Exception) {

                Log.e("TCP", "S: Error", e)

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close()
            }

        } catch (e: Exception) {

            Log.e("TCP", "C: Error", e)

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    interface OnMessageReceived {
        fun messageReceived(message: String)
    }

    companion object {
        val SERVERIP = "192.168.5.1" //your computer IP address
        val SERVERPORT = 22
    }
}