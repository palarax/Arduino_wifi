package palarax.arduino_wifi.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import palarax.arduino_wifi.R
import palarax.arduino_wifi.`interface`.BluetoothFragContract
import palarax.arduino_wifi.presenter.BluetoothPresenter
import palarax.arduino_wifi.services.Bluetooth


/**
 * Created by Ithai on 4/07/2017.
 */
class BluetoothFragment : Fragment(), BluetoothFragContract.View {

    val mPresenter: BluetoothPresenter by lazy { BluetoothPresenter(this) }

    private val TAG: String = BluetoothFragment::class.java.simpleName
    private val PERMISSIONS_REQUEST_CODE = 101

    private var mBluetooth: Bluetooth = Bluetooth(activity)

    //attaches an acitivity
    override fun onAttach(activity: Activity) {
        Log.e(TAG, "onAttach")
        super.onAttach(activity)

    }

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
        mBluetooth.enableBluetooth()

    }


    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        //Log.e(TAG,"Clicked on "+devices[position].name)
        //bluetooth.pair(devices[position])
        //bluetooth.connectToDevice(devices[position])
        //adapter.clear()
        //btnScan.isEnabled = false
        //val uuids: Array <ParcelUuid>  = devices[position].uuids
        //uuids.map { it }.forEach { Log.e(TAG,"UUID: "+ it) }

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
                && (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED)) {
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
                Log.e(TAG, "PERMISSION GRANTED")
                mBluetooth.enableBluetooth()
            }
        }
    }

}