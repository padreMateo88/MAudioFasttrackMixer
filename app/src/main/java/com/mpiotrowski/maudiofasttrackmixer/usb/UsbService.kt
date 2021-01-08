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
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil

const val DEVICE_INTENT_EXTRA = "deviceIntentExtra"
const val USB_PRODUCT_ID = 0x2081
const val USB_VENDOR_ID = 0x0763
const val SERVICE_NOTIFICATION_ID = 1863
const val NOTIFICATION_CHANNEL_ID = "FastTrack_Ultra_8R"
const val NOTIFICATION_CHANNEL_NAME = "FastTrack Ultra 8R notification"

class UsbService: Service() {

    private var device: UsbDevice? = null

    private var usbHelper: UsbHelper? = null

    private var usbDetachedReceiver: BroadcastReceiver? = null
    private val binder = UsbServiceBinder()

    inner class UsbServiceBinder : Binder() {
        fun getUsbService(): UsbService = this@UsbService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        registerDetachReceiver()
        moveServiceToForeground()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        device = intent.getParcelableExtra(DEVICE_INTENT_EXTRA)
        device?.let {
            usbHelper = UsbHelper()
            usbHelper!!.connectDevice(applicationContext, device!!)
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
                LogUtil.d("USB DEVICE DETACHED")
                val action = intent.action
                if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                    if(device?.vendorId != USB_VENDOR_ID || device.productId != USB_PRODUCT_ID)
                        return

                    usbHelper?.disconnectDevice()
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

    fun setChannelVolume(volume: Int, pan: Int, input: Int, outputPair: Int, masterVolume: Int, masterPan: Int, mute: Boolean) {
        usbHelper?.setChannelVolume(volume, pan,input, outputPair, masterVolume, masterPan, mute)
    }

    fun setFxSend(sendValue: Int, input: Int) {
        usbHelper?.setFxSend(sendValue, input)
    }

    fun setFxReturn(fxReturnValue: Int, outputPair: Int) {
        usbHelper?.setFxReturn(fxReturnValue, outputPair)
    }

    //1..127 linear
    fun setFxVolume(value: Int) {
        usbHelper?.setFxVolume(value)
    }

    fun setFxDuration(value: Int) {
        usbHelper?.setFxDuration(value)
    }

    fun setFxFeedback(value: Int) {
        usbHelper?.setFxFeedback(value)
    }

    fun setNextFxType(): FxType? {
        return usbHelper?.setFxType()
    }

    fun setSampleRate(): SampleRate? {
        return usbHelper?.setSampleRate()
    }
}