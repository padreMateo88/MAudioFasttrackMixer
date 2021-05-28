package com.mpiotrowski.maudiofasttrackmixer.usb

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity

const val DEVICE_INTENT_EXTRA = "deviceIntentExtra"
const val USB_PRODUCT_ID = 0x2081
const val USB_VENDOR_ID = 0x0763
const val SERVICE_NOTIFICATION_ID = 1863
const val NOTIFICATION_CHANNEL_ID = "FastTrack_Ultra_8R"
const val NOTIFICATION_CHANNEL_NAME = "FastTrack Ultra 8R notification"

class UsbService: Service() {

    private var usbController: UsbController? = null

    private var usbDetachedReceiver: BroadcastReceiver? = null
    private val binder = UsbServiceBinder()

    inner class UsbServiceBinder : Binder() {
        fun getUsbConnection(): UsbController? = usbController
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        registerDetachReceiver()
        moveServiceToForeground()
    }

    private fun setDeviceConnectedBroadcast() {
        val intent = Intent(MainActivity.deviceConnectedAction)
        sendBroadcast(intent)

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val device: UsbDevice? = intent.getParcelableExtra(DEVICE_INTENT_EXTRA)
        device?.let {
            usbController = UsbController()
            usbController!!.connectDevice(applicationContext, device)
            setDeviceConnectedBroadcast()
        } ?: stopSelf()

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterDetachReceiver()
    }

    private fun registerDetachReceiver() {
        if(usbDetachedReceiver != null)
            return

        usbDetachedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if(device?.vendorId != USB_VENDOR_ID || device.productId != USB_PRODUCT_ID)
                        return

                    usbController?.disconnectDevice()
                    usbController = null
                    stopSelf()
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbDetachedReceiver, filter)
    }

    private fun unregisterDetachReceiver() {
        usbDetachedReceiver?.let{
            unregisterReceiver(usbDetachedReceiver)
            usbDetachedReceiver = null
        }
    }

    private fun moveServiceToForeground() {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)

            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("FastTrack 8R is connected")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("FastTrack 8R title")
                .setContentText("FastTrack 8R content")
                .setContentIntent(pendingIntent)
                .setTicker("FastTrack ticker")
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("FastTrack 8R is connected")
                .setContentIntent(pendingIntent)
                .build()
        }

        startForeground(SERVICE_NOTIFICATION_ID, notification)
    }
}