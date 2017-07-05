package palarax.arduino_wifi

import android.app.Activity
import android.app.Fragment
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.bluetooth_fragment.*
import me.aflak.bluetooth.Bluetooth





/**
 * Created by Ithai on 4/07/2017.
 */
class BluetoothFragment: Fragment(), AdapterView.OnItemClickListener,Bluetooth.DiscoveryCallback,Bluetooth.CommunicationCallback{

    val TAG = "BluetoothFragment"

    lateinit var bluetooth : Bluetooth
    lateinit var adapter : ArrayAdapter<String>
    lateinit var listView : ListView

    var devices: List<BluetoothDevice>? = ArrayList()


    //attaches an acitivity
    override fun onAttach(activity: Activity) {
        Log.e(TAG,"onAttach")
        super.onAttach(activity)
        bluetooth = Bluetooth(activity)
        bluetooth.enableBluetooth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG,"onCreateView")
        return inflater?.inflate(R.layout.bluetooth_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"onActivityCreated")
        //create ListView to see scanned Bluetooth devices
        listView = view.findViewById(R.id.scan_list) as ListView
        adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, ArrayList<String>())
        listView.adapter = adapter
        listView.onItemClickListener = this
        listView.isEnabled =false

        //set Bluetooth Callback
        bluetooth.setCommunicationCallback(this)
        bluetooth.setDiscoveryCallback(this)

        val btnScan = view.findViewById(R.id.btnScan) as Button
        btnScan.setOnClickListener({
            // Handler code here.
            devices = ArrayList()
            adapter.clear()
            btnScan.isEnabled=false
            bluetooth.scanDevices()

            Toast.makeText(activity, "Scanning",
                    Toast.LENGTH_SHORT).show()
        })

        val btnConnect = view.findViewById(R.id.btnConnect) as Button
        btnConnect.setOnClickListener({
            // Handler code here.
            Toast.makeText(activity, "Connecting",
                    Toast.LENGTH_SHORT).show()
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(activity, "Item clicked",
                Toast.LENGTH_SHORT).show()

    }

    //BLUETOOTH DISCOVERY
    override fun onFinish() {
        // scan finished
        btnScan.isEnabled=true
        listView.isEnabled =true
    }

    override fun onDevice(device: BluetoothDevice) {
        // device found
        adapter.add(device.address +" - "+device.name)
    }

    override fun onPair(device: BluetoothDevice) {
        // device paired
    }

    override fun onUnpair(device: BluetoothDevice) {
        // device unpaired
    }



    //BLUETOOTH COMMUNICATE
    override fun onConnect(device: BluetoothDevice) {
        // device connected
    }

    override fun onDisconnect(device: BluetoothDevice, message: String) {
        // device disconnected
    }

    override fun onMessage(message: String) {
        // message received (it has to end with a \n to be received)
    }

    //In both Communication and discovery callback
    override fun onError(message: String) {
        // error occurred
    }

    override fun onConnectError(device: BluetoothDevice, message: String) {
        // error during connection
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG,"onResume")
        bluetooth.enableBluetooth()
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG,"onStart")
        bluetooth.enableBluetooth()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"onDestroy")
        bluetooth.disableBluetooth()
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG,"onStop")
        bluetooth.disableBluetooth()
    }




}