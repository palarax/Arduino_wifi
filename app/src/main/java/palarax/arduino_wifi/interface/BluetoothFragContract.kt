package palarax.arduino_wifi.`interface`

/**
 * Created by Ithai on 17/11/2017.
 */
object BluetoothFragContract {

    interface View

    interface Presenter {


        /**
         * Connect to a bluetooth device
         */
        fun connectToBluetooth()

        /**
         * Disconnect from a bluetooth device
         */
        fun disconnectFromBluetooth()


        /**
         * Transmit some data to the bluetooth device
         */
        fun transmitData()


        /**
         * Receive data from the bluetooth device
         */
        fun receiveData()

    }
}