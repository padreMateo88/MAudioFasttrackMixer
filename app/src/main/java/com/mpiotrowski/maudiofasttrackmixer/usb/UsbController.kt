package com.mpiotrowski.maudiofasttrackmixer.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
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

    fun setFxType(fxType: FxSettings.FxType) {
        usbConnection?.setFxType(fxType)
    }

    fun setSampleRate(sampleRate: SampleRate) {
        usbConnection?.setSampleRate(sampleRate)
    }

    fun loadMixerState(scene: SceneWithComponents) {

        setFxFeedback(scene.scene.fxSettings.feedback)
        setFxType(scene.scene.fxSettings.fxType)
        setFxVolume(scene.scene.fxSettings.volume)
        setFxDuration(scene.scene.fxSettings.duration)

        for ((_, audioChannels) in scene.channelsByOutputsMap) {
            for(audioChannel in audioChannels) {
                val masterChannel = scene.mastersByOutputsMap[audioChannel.outputIndex]

                val mute = when {
                    masterChannel?.mute ?: false -> true
                    audioChannel.solo -> false
                    isAnySolo(audioChannels) -> true
                    else -> audioChannel.mute
                }

                setChannelVolume(audioChannel.volume * 100, audioChannel.panorama, audioChannel.inputIndex,
                    audioChannel.outputIndex,  (masterChannel?.volume  ?: 0) * 100,
                    masterChannel?.panorama ?: 0, mute
                )
            }
        }

        for(fxSend in scene.fxSends) {
            setFxSend(fxSend.volume * 100, fxSend.inputIndex)
        }

        for(master in scene.masterChannels) {
            setFxReturn(master.fxReturn * 100, master.outputIndex)
        }
    }

    private fun isAnySolo(audioChannels : List<AudioChannel>): Boolean {
        for(audioChannel in audioChannels) {
            if(audioChannel.solo)
                return true
        }
        return false
    }
}