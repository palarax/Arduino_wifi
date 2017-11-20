package palarax.arduino_wifi.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.bluetooth_fragment.*
import palarax.arduino_wifi.R
import palarax.arduino_wifi.`interface`.BluetoothFragContract
import palarax.arduino_wifi.presenter.BluetoothPresenter
import palarax.arduino_wifi.services.Bluetooth


/**
 * Created by Ithai on 4/07/2017.
 */
class BluetoothFragment : Fragment(), BluetoothFragContract.View, Bluetooth.DiscoveryCallback,
        Bluetooth.CommunicationCallback, AdapterView.OnItemClickListener {


    val mPresenter: BluetoothPresenter by lazy { BluetoothPresenter(this) }
    private val PERMISSIONS_REQUEST_CODE = 101

    private val TAG: String = BluetoothFragment::class.java.simpleName

    private lateinit var mBluetooth: Bluetooth

    private var mDeviceList: ArrayList<String> = ArrayList()

    private lateinit var mBluetoothListAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG, "onCreateView")
        return inflater?.inflate(R.layout.bluetooth_fragment, container, false)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated")

        checkPermissions()

        mBluetooth = Bluetooth(activity)
        mBluetooth.enableBluetooth()

        mBluetooth.setDiscoveryCallback(this)
        mBluetooth.setCommunicationCallback(this)

        mBluetoothListAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, mDeviceList)
        bluetooth_list_scan.adapter = mBluetoothListAdapter
        bluetooth_list_scan.onItemClickListener = this

        btnSend.setOnClickListener {
            Toast.makeText(context, "WORKED", Toast.LENGTH_SHORT).show()
            mBluetooth.send(sendMsg.text.toString())
        }

        btnScan.setOnClickListener {
            mBluetooth.scanDevices()
        }


    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //mBluetooth.removeDiscoveryCallback()
        Log.e(TAG, "Clicked on " + parent!!.getItemAtPosition(position))
        mBluetooth.connectToName(parent.getItemAtPosition(position).toString())
        //mBluetooth.pair
        btnScan.isEnabled = false
    }


    /* --------------------------Bluetooth SETUP --------------------------------------------*/
    override fun onFinish() {
        mBluetooth.removeDiscoveryCallback()
    }

    override fun onDevice(device: BluetoothDevice?) {
        mDeviceList.add(device!!.name)
        mBluetoothListAdapter.notifyDataSetChanged()

    }

    override fun onPair(device: BluetoothDevice?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUnpair(device: BluetoothDevice?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(message: String?) {
        Log.e(TAG, "BLUETOOTH ERROR")
    }


    override fun onConnect(device: BluetoothDevice?) {
        activity.runOnUiThread {
            Toast.makeText(activity, "Connected to: " + device!!.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDisconnect(device: BluetoothDevice?, message: String?) {
        Log.e(TAG, String.format("Disconnected from %s error: %s", device!!.name, message))
    }

    override fun onMessage(message: String?) {
        Log.e(TAG, "Message: " + message)
    }

    override fun onConnectError(device: BluetoothDevice?, message: String?) {
        Log.e(TAG, String.format("Error connecting to %s with error: %s", device!!.name, message))
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

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_CODE)
            false
        } else {
            true
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                //startScanning here
                mBluetooth.enableBluetooth()
            }
        }
    }


}