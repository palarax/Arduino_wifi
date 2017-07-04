package palarax.arduino_wifi.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by Ithai on 4/07/2017.
 */
class BluetoothService: Service() {

    var mStartMode: Int = 0       // indicates how to behave if the service is killed
    var mBinder: IBinder? = null      // interface for clients that bind
    var mAllowRebind: Boolean = false // indicates whether onRebind should be used

    override fun onCreate() {
        // The service is being created
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // The service is starting, due to a call to startService()
        return mStartMode
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // All clients have unbound with unbindService()
        return mAllowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    override fun onDestroy() {
        // The service is no longer used and is being destroyed
    }

}
