package palarax.arduino_wifi

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.bluetooth_fragment.*
import java.util.*


/**
 * Created by Ithai on 4/07/2017.
 */
class BluetoothFragment : Fragment(), AdapterView.OnItemClickListener, palarax.arduino_wifi.services.Bluetooth.DiscoveryCallback, palarax.arduino_wifi.services.Bluetooth.CommunicationCallback {

    val TAG = "BluetoothFragment"

    val PERMISSIONS_REQUEST_CODE = 120

    lateinit var bluetooth: palarax.arduino_wifi.services.Bluetooth
    lateinit var adapter: ArrayAdapter<String>
    lateinit var listView: ListView
    lateinit var sendMsg: EditText


    var devices: MutableList<BluetoothDevice> = mutableListOf()


    //attaches an acitivity
    override fun onAttach(activity: Activity) {
        Log.e(TAG, "onAttach")
        super.onAttach(activity)
        bluetooth = palarax.arduino_wifi.services.Bluetooth(activity)
        bluetooth.enableBluetooth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG, "onCreateView")
        return inflater?.inflate(R.layout.bluetooth_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated")
        checkPermissions()
        //create ListView to see scanned Bluetooth devices
        listView = view.findViewById(R.id.scan_list) as ListView
        adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, ArrayList<String>())
        listView.adapter = adapter
        listView.onItemClickListener = this
        listView.isEnabled = false
        sendMsg = view.findViewById(R.id.sendMsg) as EditText

        //set Bluetooth Callback
        bluetooth.setCommunicationCallback(this)
        bluetooth.setDiscoveryCallback(this)


        val btnScan = view.findViewById(R.id.btnScan) as Button
        btnScan.setOnClickListener({
            // Handler code here.
            Log.e(TAG, "SCANNING")
            adapter.clear()
            devices.clear()
            btnScan.isEnabled = false
            bluetooth.scanDevices()

            Toast.makeText(activity, "Scanning",
                    Toast.LENGTH_SHORT).show()
        })

        val btnSend = view.findViewById(R.id.btnSend) as Button
        btnSend.setOnClickListener({
            // Handler code here.
            Log.e(TAG,"Socket: "+bluetooth.socket)
            Log.e(TAG,"Paired Devices: "+bluetooth.pairedDevices)

            bluetooth.send(sendMsg.text.toString())

        })
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !== android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_CODE)
            return false
        } else {
            return true
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                //startScanning here
                Log.e(TAG, "PERMISSION GRANTED")
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        Log.e(TAG,"Clicked on "+devices[position].name)
        //bluetooth.pair(devices[position])
        bluetooth.connectToDevice(devices[position])
        adapter.clear()
        //btnScan.isEnabled = false
        //val uuids: Array <ParcelUuid>  = devices[position].uuids
        //uuids.map { it }.forEach { Log.e(TAG,"UUID: "+ it) }

    }

    //BLUETOOTH DISCOVERY
    override fun onFinish() {
        // scan finished
        btnScan.isEnabled = true
        listView.isEnabled = true
    }

    override fun onDevice(device: BluetoothDevice) {
        // device found
        Log.e(TAG, "Device: " + device.name + " " + device.address)
        devices.add(device)
        adapter.add(device.address + " - " + device.name)
    }

    override fun onPair(device: BluetoothDevice) {
        // device paired
        Log.e(TAG,"Pairing with "+device.name)
        bluetooth.connectToDevice(device)
    }

    override fun onUnpair(device: BluetoothDevice) {
        // device unpaired
    }


    //BLUETOOTH COMMUNICATE
    override fun onConnect(device: BluetoothDevice) {
        // device connected
        Log.e(TAG, "connected to device")
        activity.runOnUiThread({
            Toast.makeText(activity, "Connected to " + device.name,
                    Toast.LENGTH_SHORT).show()
            listView.visibility = View.INVISIBLE
            btnScan.isEnabled = false
        })

    }

    override fun onDisconnect(device: BluetoothDevice, message: String) {
        // device disconnected
        Log.e(TAG, "disconnected from device")
        activity.runOnUiThread({
            listView.visibility = View.VISIBLE
        })
    }

    override fun onMessage(message: String) {
        // message received (it has to end with a \n to be received)
        Log.e(TAG, "MESSAGE RECEIVED")
    }

    //In both Communication and discovery callback
    override fun onError(message: String) {
        // error occurred
        /*Toast.makeText(activity, "Error occured",
                Toast.LENGTH_SHORT).show()*/
    }

    override fun onConnectError(device: BluetoothDevice, message: String) {
        Log.e(TAG,"Connection Error")
        activity.runOnUiThread({
            val handler = Handler()
            handler.postDelayed({ bluetooth.connectToDevice(device) }, 2000)
        })

    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")

        //bluetooth.enableBluetooth()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
        bluetooth.disableBluetooth()
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop")

    }

}