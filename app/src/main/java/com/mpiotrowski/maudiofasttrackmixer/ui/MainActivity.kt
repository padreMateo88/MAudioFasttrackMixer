package com.mpiotrowski.maudiofasttrackmixer.ui

import android.content.*
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.usb.DEVICE_INTENT_EXTRA
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbController
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbService
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        const val deviceConnectedAction = "com.mpiotrowski.maudiofasttrackmixer.deviceConnected"
    }

    private var usbServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            viewModel.deviceOnline.postValue(true)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            viewModel.deviceOnline.value = false
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var usbController: UsbController


    private val viewModel by viewModels<MainViewModel> {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupBottomNavMenu(navController)
        connectUsb(intent)
    }

    private fun connectUsb(intent: Intent?) {

        val device: UsbDevice? = intent?.getParcelableExtra(UsbManager.EXTRA_DEVICE)

        if (device != null) {
            Intent(this@MainActivity, UsbService::class.java).also { serviceIntent ->
                serviceIntent.putExtra(DEVICE_INTENT_EXTRA, device)
                (this@MainActivity).applicationContext.startService(serviceIntent)
            }
        }

        Intent(this, UsbService::class.java).also { intent ->
            bindService(intent, usbServiceConnection as ServiceConnection, 0)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        connectUsb(intent)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottom_nav.setupWithNavController(navController)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveCurrentDeviceState()
    }

}