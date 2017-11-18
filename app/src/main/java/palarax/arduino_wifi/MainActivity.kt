package palarax.arduino_wifi

import android.app.Fragment
import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import palarax.arduino_wifi.fragment.BluetoothFragment
import palarax.arduino_wifi.fragment.WifiFragment


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private val wifi_fragment = fragmentManager.findFragmentByTag("wifi_fragment") ?: WifiFragment()
    private val home_fragment = fragmentManager.findFragmentByTag("home_fragment") ?: MainFragment()
    private val bluetooth_fragment = fragmentManager.findFragmentByTag("bluetooth_fragment") ?: BluetoothFragment()


    //is wifi enabled
    var isEnabled: Boolean? = false

    //Channels for WIFI P2P
    var mChannel: WifiP2pManager.Channel? = null

    //Wifi manager
    private val mP2Pmanger: WifiP2pManager
        get() = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    //listens to events in onCreate
    //private val intentFilter = IntentFilter()


    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        this.isEnabled = isWifiP2pEnabled
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        /*val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //Initial fragment
        fragmentManager.beginTransaction()
        .replace(R.id.main_frag,home_fragment)
                .commit()

    }


    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    /*override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        R.id.action_settings -> consume { null }
        //R.id.nav_camera -> drawer.consume { navigateToCamera() }
        else -> super.onOptionsItemSelected(item)
    }*/

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_home -> fragmentManager.beginTransaction()
                    .replace(R.id.main_frag,home_fragment)
                    .commit()
            R.id.nav_wifi -> fragmentManager.beginTransaction()
                    .replace(R.id.main_frag, wifi_fragment)
                    .commit()
            R.id.nav_bluetooth -> fragmentManager.beginTransaction()
                    .replace(R.id.main_frag,bluetooth_fragment)
                    .commit()
            R.id.nav_controls -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    class MainFragment : Fragment(){


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater?.inflate(R.layout.home_fragment,container,false)
        }
    }


}
