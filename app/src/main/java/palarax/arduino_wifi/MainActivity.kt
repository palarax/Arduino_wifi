package palarax.arduino_wifi

import android.app.Fragment
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private val wifi_fragment = fragmentManager.findFragmentByTag("wifi_fragment") ?: WifiFragment()
    private val home_fragment = fragmentManager.findFragmentByTag("home_fragment") ?: MainFragment()
    private val bluetooth_fragment = fragmentManager.findFragmentByTag("bluetooth_fragment") ?: BluetoothFragment()

    var wifiReceiver: WifiReceiver? = null

    private val peers = ArrayList<WifiP2pDevice>()

    //is wifi enabled
    var isEnabled: Boolean? = false

    //Channels for WIFI P2P
    var mChannel: WifiP2pManager.Channel? = null

    //Wifi manager
    private val mP2Pmanger: WifiP2pManager
        get() = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    //listens to events in onCreate
    private val intentFilter = IntentFilter()


    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        this.isEnabled = isWifiP2pEnabled
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        //Initial fragment
        fragmentManager.beginTransaction()
        .replace(R.id.main_frag,home_fragment)
                .commit()

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)        //Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)   //Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)  //Indicates this device's details have changed.

        mChannel = mP2Pmanger.initialize(this, Looper.getMainLooper(), null)    //return channel which is used to connect to P2P framework
        wifiReceiver = WifiReceiver(mP2Pmanger, mChannel, this)
    }

    fun discoverPeers()
    {
        mP2Pmanger.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            override fun onFailure(reasonCode: Int) {
                Toast.makeText(baseContext, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        wifiReceiver = WifiReceiver(mP2Pmanger, mChannel, this)
        registerReceiver(wifiReceiver, intentFilter)
    }


    override fun onResume() {
        super.onResume()
        wifiReceiver = WifiReceiver(mP2Pmanger, mChannel, this)
        registerReceiver(wifiReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(wifiReceiver)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        R.id.action_settings -> consume { null }
        //R.id.nav_camera -> drawer.consume { navigateToCamera() }
        else -> super.onOptionsItemSelected(item)
    }

    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_frag,home_fragment)
                    .commit()

        } else if (id == R.id.nav_wifi) {
            fragmentManager.beginTransaction()
            .replace(R.id.main_frag,wifi_fragment)
                    .commit()
        } else if (id == R.id.nav_bluetooth) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_frag,bluetooth_fragment)
                    .commit()
        } else if (id == R.id.nav_controls) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    class MainFragment : Fragment(){


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater?.inflate(R.layout.home_fragment,container,false)
        }
    }

    class WifiFragment : Fragment(){


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater?.inflate(R.layout.wifi_fragment,container,false)
        }
    }

}
