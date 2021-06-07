package com.mpiotrowski.maudiofasttrackmixer.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsbController @Inject constructor() {

    private var usbConnection: UsbConnectionHelper? = null

    fun connectDevice(context: Context, device: UsbDevice) {
        usbConnection = UsbConnectionHelper()
        usbConnection?.connectDevice(context, device)
    }

    fun disconnectDevice() {
        usbConnection?.disconnectDevice()
        usbConnection = null
    }

    fun setChannelVolume(volume: Int, pan: Int, input: Int, outputPair: Int, masterVolume: Int, masterPan: Int, mute: Boolean) {
        usbConnection?.setChannelVolume(volume, pan,input, outputPair, masterVolume, masterPan, mute)
    }

    fun setFxSend(sendValue: Int, input: Int) {
        usbConnection?.setFxSend(sendValue, input)
    }

    fun setFxReturn(fxReturnValue: Int, outputPair: Int) {
        usbConnection?.setFxReturn(fxReturnValue, outputPair)
    }

    //1..127 linear
    fun setFxVolume(value: Int) {
        usbConnection?.setFxVolume(value)
    }

    fun setFxDuration(value: Int) {
        usbConnection?.setFxDuration(value)
    }

    fun setFxFeedback(value: Int) {
        usbConnection?.setFxFeedback(value)
    }

    fun setNextFxType(fxType: FxType) {
        usbConnection?.setFxType(fxType)
    }

    fun setSampleRate(sampleRate: SampleRate) {
        usbConnection?.setSampleRate(sampleRate)
    }

    fun loadMixerState(scene: Scene) {}
}