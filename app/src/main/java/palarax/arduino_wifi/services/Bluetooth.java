package palarax.arduino_wifi.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Omar on 14/07/2015.
 */
public class Bluetooth {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device, devicePair;
    private BufferedReader input;
    private OutputStream out;

    private boolean connected = false;
    private CommunicationCallback communicationCallback = null;
    private DiscoveryCallback discoveryCallback = null;

    private Activity activity;

    public Bluetooth(Activity activity) {
        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Enable bluetooth on android
     */
    public void enableBluetooth() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                Log.e("TEST", "ENABLING");
            }
            Log.e("TEST", "ENABLED");
        } else Log.e("TEST", "DISABLED");
    }

    /**
     * Disable bluetooth on android
     */
    public void disableBluetooth() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
            }
        }
    }

    /**
     * Connect to bluetooth device with a bluetooth address
     *
     * @param address connect to a device with this address
     */
    public void connectToAddress(String address) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        new ConnectThread(device).start();
    }

    /**
     * Connect to bluetooth device with a bluetooth name
     * @param name connect to a device with this name
     */
    public void connectToName(String name) {
        for (BluetoothDevice blueDevice : bluetoothAdapter.getBondedDevices()) {
            if (blueDevice.getName().equals(name)) {
                connectToAddress(blueDevice.getAddress());
                return;
            }
        }
    }

    /**
     * connect to a device
     * @param device device to connect to
     */
    public void connectToDevice(BluetoothDevice device) {
        new ConnectThread(device).start();
    }


    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            if (communicationCallback != null)
                communicationCallback.onError(e.getMessage());
        }
    }

    /**
     * Is android connected to a bluetooth device
     * @return true if connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * send a string to the bluetooth device
     * @param msg message to send
     */
    public void send(String msg) {
        try {
            out.write(msg.getBytes());
        } catch (IOException e) {
            connected = false;
            if (communicationCallback != null)
                communicationCallback.onDisconnect(device, e.getMessage());
        }
    }


    private class ReceiveThread extends Thread implements Runnable {
        public void run() {
            String msg;
            try {
                while ((msg = input.readLine()) != null) {
                    if (communicationCallback != null)
                        communicationCallback.onMessage(msg);
                }
            } catch (IOException e) {
                connected = false;
                if (communicationCallback != null)
                    communicationCallback.onDisconnect(device, e.getMessage());
            }
        }
    }

    private class ConnectThread extends Thread {
        public ConnectThread(BluetoothDevice device) {
            Bluetooth.this.device = device;
            try {
                Bluetooth.this.socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                if (communicationCallback != null)
                    communicationCallback.onError(e.getMessage());
            }
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                out = socket.getOutputStream();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;

                new ReceiveThread().start();

                if (communicationCallback != null)
                    communicationCallback.onConnect(device);
            } catch (IOException e) {
                if (communicationCallback != null)
                    communicationCallback.onConnectError(device, e.getMessage());

                try {
                    socket.close();
                } catch (IOException closeException) {
                    if (communicationCallback != null)
                        communicationCallback.onError(closeException.getMessage());
                }
            }
        }
    }

    public List<BluetoothDevice> getPairedDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        for (BluetoothDevice blueDevice : bluetoothAdapter.getBondedDevices()) {
            devices.add(blueDevice);
        }
        return devices;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    /**
     * Scan for Bluetooth devices
     */
    public void scanDevices() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        activity.registerReceiver(mReceiverScan, filter);
        bluetoothAdapter.startDiscovery();
    }

    /**
     * Pair with a bluetooth device
     * @param device bluetooth device to pair with
     */
    public void pair(BluetoothDevice device) {
        activity.registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair = device;
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            if (discoveryCallback != null)
                discoveryCallback.onError(e.getMessage());
        }
    }

    /**
     * Unpair with a bluetooth device
     * @param device bluetooth device to unpair with
     */
    public void unpair(BluetoothDevice device) {
        devicePair = device;
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            if (discoveryCallback != null)
                discoveryCallback.onError(e.getMessage());
        }
    }

    private BroadcastReceiver mReceiverScan = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_OFF) {
                        if (discoveryCallback != null)
                            discoveryCallback.onError("Bluetooth turned off");
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    context.unregisterReceiver(mReceiverScan);
                    if (discoveryCallback != null)
                        discoveryCallback.onFinish();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (discoveryCallback != null)
                        discoveryCallback.onDevice(device);
                    break;
            }
        }
    };

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    context.unregisterReceiver(mPairReceiver);
                    if (discoveryCallback != null)
                        discoveryCallback.onPair(devicePair);
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    context.unregisterReceiver(mPairReceiver);
                    if (discoveryCallback != null)
                        discoveryCallback.onUnpair(devicePair);
                }
            }
        }
    };

    public interface CommunicationCallback {
        void onConnect(BluetoothDevice device);

        void onDisconnect(BluetoothDevice device, String message);

        void onMessage(String message);

        void onError(String message);

        void onConnectError(BluetoothDevice device, String message);
    }


    public void setCommunicationCallback(CommunicationCallback communicationCallback) {
        this.communicationCallback = communicationCallback;
    }

    public void removeCommunicationCallback() {
        this.communicationCallback = null;
    }

    public interface DiscoveryCallback {
        void onFinish();

        void onDevice(BluetoothDevice device);

        void onPair(BluetoothDevice device);

        void onUnpair(BluetoothDevice device);

        void onError(String message);
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    public void removeDiscoveryCallback() {
        this.discoveryCallback = null;
    }

}
