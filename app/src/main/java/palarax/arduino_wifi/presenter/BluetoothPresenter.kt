package palarax.arduino_wifi.presenter

import palarax.arduino_wifi.`interface`.BluetoothFragContract

/**
 * Created by Ithai on 17/11/2017.
 */
class BluetoothPresenter(var view: BluetoothFragContract.View) : BluetoothFragContract.Presenter {

    private var mView: BluetoothFragContract.View? = null


    private val TAG: String = BluetoothPresenter::class.java.simpleName


    override fun connectToBluetooth() {

    }

    override fun disconnectFromBluetooth() {

    }

    override fun transmitData() {

    }

    override fun receiveData() {

    }


//
//    /* --------------------------BLUETOOTH SETUP --------------------------------------------*/
//    //BLUETOOTH DISCOVERY
//    override fun onFinish() {
//        // scan finished
//        btnScan.isEnabled = true
//        listView.isEnabled = true
//    }
//
//    override fun onDevice(device: BluetoothDevice) {
//        // device found
//        Log.e(TAG, "Device: " + device.name + " " + device.address)
//        devices.add(device)
//        adapter.add(device.address + " - " + device.name)
//    }
//
//    override fun onPair(device: BluetoothDevice) {
//        // device paired
//        Log.e(TAG,"Pairing with "+device.name)
//        bluetooth.connectToDevice(device)
//    }
//
//    override fun onUnpair(device: BluetoothDevice) {
//        // device unpaired
//    }
//
//
//    //BLUETOOTH COMMUNICATE
//    override fun onConnect(device: BluetoothDevice) {
//        // device connected
//        Log.e(TAG, "connected to device")
//        activity.runOnUiThread({
//            Toast.makeText(activity, "Connected to " + device.name,
//                    Toast.LENGTH_SHORT).show()
//            listView.visibility = View.INVISIBLE
//            btnScan.isEnabled = false
//            btnController.isEnabled = true
//        })
//
//    }
//
//    override fun onDisconnect(device: BluetoothDevice, message: String) {
//        // device disconnected
//        Log.e(TAG, "disconnected from device")
//        activity.runOnUiThread({
//            listView.visibility = View.VISIBLE
//        })
//    }
//
//    override fun onMessage(message: String) {
//        // message received (it has to end with a \n to be received)
//        Log.e(TAG, "MESSAGE RECEIVED")
//    }
//
//    //In both Communication and discovery callback
//    override fun onError(message: String) {
//        // error occurred
//        /*Toast.makeText(activity, "Error occured",
//                Toast.LENGTH_SHORT).show()*/
//    }
//
//    override fun onConnectError(device: BluetoothDevice, message: String) {
//        Log.e(TAG,"Connection Error")
//        activity.runOnUiThread({
//            val handler = Handler()
//            handler.postDelayed({ bluetooth.connectToDevice(device) }, 2000)
//        })
//
//    }
}