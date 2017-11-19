package palarax.arduino_wifi.fragment

import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.bluetooth.BluetoothDevice
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
class BluetoothFragment : Fragment(), BluetoothFragContract.View, Bluetooth.DiscoveryCallback {

    val mPresenter: BluetoothPresenter by lazy { BluetoothPresenter(this) }

    private val TAG: String = BluetoothFragment::class.java.simpleName

    private var mBluetooth: Bluetooth = Bluetooth(activity)

    private var mDeviceList: ArrayList<String> = ArrayList()

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
        //Location permissions might need to be needed for bluetooth, so far not needed
        mBluetooth.enableBluetooth()

        btnSend.setOnClickListener {
            Toast.makeText(context, "WORKED", Toast.LENGTH_SHORT).show()
        }


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

    /* --------------------------Bluetooth SETUP --------------------------------------------*/
    override fun onFinish() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDevice(device: BluetoothDevice?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        bluetooth_list_scan.adapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, mDeviceList)
    }

    override fun onPair(device: BluetoothDevice?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUnpair(device: BluetoothDevice?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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



}