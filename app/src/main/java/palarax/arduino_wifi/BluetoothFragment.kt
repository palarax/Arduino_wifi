package palarax.arduino_wifi

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import me.aflak.bluetooth.Bluetooth






/**
 * Created by Ithai on 4/07/2017.
 */
class BluetoothFragment: Fragment() {

    val TAG = "BluetoothFragment"
    //lateinit var activity : FragmentActivity
    lateinit var bluetooth : Bluetooth

    //attaches an acitivity
    override fun onAttach(activity: Activity) {
        Log.e(TAG,"onAttach")
        super.onAttach(activity)
        bluetooth = Bluetooth(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG,"onCreate")
        super.onCreate(savedInstanceState)
        bluetooth.enableBluetooth()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG,"onCreateView")
        return inflater?.inflate(R.layout.bluetooth_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG,"onActivityCreated")
        val btnConnect = view.findViewById(R.id.btnBluetooth) as Button
        btnConnect.setOnClickListener({
            // Handler code here.
            Toast.makeText(activity, "Connected",
                    Toast.LENGTH_SHORT).show()
        })
    }



    override fun onResume() {
        super.onResume()
        bluetooth.enableBluetooth()
    }

    override fun onStart() {
        super.onStart()
        bluetooth.enableBluetooth()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetooth.disableBluetooth()
    }

    override fun onStop() {
        super.onStop()
        bluetooth.disableBluetooth()
    }


}