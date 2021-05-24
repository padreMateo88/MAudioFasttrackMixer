package com.mpiotrowski.maudiofasttrackmixer.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbController
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbService
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {


    private var usbController: UsbController? = null

    companion object {
        const val deviceConnectedAction = "com.mpiotrowski.maudiofasttrackmixer.deviceConnected"
    }

    private var deviceConnectionReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null && intent.action == deviceConnectedAction) {
                bindUsbService()
            }
        }
    }

    private var usbServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UsbService.UsbServiceBinder
            usbController = binder.getUsbConnection()
            viewModel.deviceOnline.postValue(true)
            Toast.makeText(this@MainActivity,"Activity: ${viewModel.hashCode()}", Toast.LENGTH_LONG).show()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbController = null
            viewModel.deviceOnline.value = false
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottom_nav.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        bindUsbService()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveCurrentDeviceState()
        unregisterDeviceReceiver()
    }

    private fun bindUsbService() {
        Intent(this, UsbService::class.java).also { intent ->
            bindService(intent, usbServiceConnection as ServiceConnection, 0)
        }
    }

    private fun unregisterDeviceReceiver() {
        unregisterReceiver(deviceConnectionReceiver)
    }

    private fun registerReceiver() {
        registerReceiver(
            deviceConnectionReceiver,
            IntentFilter(deviceConnectedAction)
        )
    }

}