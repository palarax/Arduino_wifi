package palarax.arduino_wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log


/**
 * Created by Ithai on 22/06/2017.
 */
class WifiReceiver(wifiManager: WifiP2pManager, channel : WifiP2pManager.Channel?, activity: MainActivity) : BroadcastReceiver() {

    companion object {
        val TAG = "Wifi_receiver"
    }


    var wifiP2Pmanager : WifiP2pManager? = wifiManager

    var mChannel : WifiP2pManager.Channel? = channel
    var mainActivity : MainActivity? = activity


    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            mainActivity?.setIsWifiP2pEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {

            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (wifiP2Pmanager != null) {
               // wifiP2Pmanager!!.requestPeers(mChannel, peerListListener);
            }
            Log.d(TAG, "P2P peers changed")

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            // Respond to new connection or disconnections

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
            // Respond to this device's wifi state changing
        }

    }




}