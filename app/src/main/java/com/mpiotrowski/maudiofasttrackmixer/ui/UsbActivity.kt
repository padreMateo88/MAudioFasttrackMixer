package com.mpiotrowski.maudiofasttrackmixer.ui

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mpiotrowski.maudiofasttrackmixer.usb.DEVICE_INTENT_EXTRA
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbService
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil

class UsbActivity : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogUtil.d( "onNewIntent")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        startService(applicationContext, device)
        finish()
    }

    private fun startService(
        context: Context,
        device: UsbDevice?
    ) {
        Intent(context, UsbService::class.java).also { serviceIntent ->
            serviceIntent.putExtra(DEVICE_INTENT_EXTRA, device)
            context.applicationContext.startService(serviceIntent)
        }
    }
}