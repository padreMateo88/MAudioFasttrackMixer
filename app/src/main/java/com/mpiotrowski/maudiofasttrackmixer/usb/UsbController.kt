package com.mpiotrowski.maudiofasttrackmixer.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbConnectionHelper.Companion.VOLUME_MIN
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsbController @Inject constructor(val usbCoroutineScope: CoroutineScope, val usbCoroutineDispatcher: CoroutineDispatcher) {

    private var usbConnection: UsbConnectionHelper? = null

    suspend fun connectDevice(context: Context, device: UsbDevice) {
        withContext(usbCoroutineDispatcher) {
            usbConnection = UsbConnectionHelper()
            usbConnection?.connectDevice(context, device)
        }
    }

    suspend fun disconnectDevice() {
        withContext(usbCoroutineDispatcher) {
            usbConnection?.disconnectDevice()
            usbConnection = null
        }
    }

    fun setChannelVolume(volume: Int, pan: Int, input: Int, outputPair: Int, masterVolume: Int, masterPan: Int, mute: Boolean) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setChannelVolume(volume, pan,input, outputPair, masterVolume, masterPan, mute)
        }
    }

    fun setFxSend(sendValue: Int, input: Int, channelVolume: Int, channelMute: Boolean ) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxSend(sendValue, input, channelVolume, channelMute)
        }
    }

    fun setFxReturn(fxReturnValue: Int, masterChannel: MasterChannel) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxReturn(
                fxReturnValue,
                masterChannel.outputIndex,
                masterChannel.volume,
                masterChannel.panorama,
                masterChannel.mute
            )
        }
    }

    //1..127 linear
    fun setFxVolume(value: Int) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxVolume(value)
        }
    }

    fun setFxDuration(value: Int) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxDuration(value)
        }
    }

    fun setFxFeedback(value: Int) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxFeedback(value)
        }
    }

    fun setFxType(fxType: FxSettings.FxType) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxType(fxType)
        }
    }

    fun setSampleRate(sampleRate: SampleRate) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setSampleRate(sampleRate)
        }
    }

    fun loadMixerState(scene: SceneWithComponents) {
        usbCoroutineScope.launch(usbCoroutineDispatcher) {
            usbConnection?.setFxType(scene.scene.fxSettings.fxType)
            usbConnection?.setFxFeedback(scene.scene.fxSettings.feedback)
            usbConnection?.setFxVolume(if (scene.scene.fxSettings.fxMute) 1 else scene.scene.fxSettings.volume)
            usbConnection?.setFxDuration(scene.scene.fxSettings.duration)
            for ((_, audioChannels) in scene.channelsByOutputsMap) {
                for (audioChannel in audioChannels) {
                    val masterChannel = scene.mastersByOutputsMap[audioChannel.outputIndex]

                    val mute = when {
                        masterChannel?.mute ?: false -> true
                        audioChannel.solo -> false
                        isAnySolo(audioChannels) -> true
                        else -> audioChannel.mute
                    }

                    usbConnection?.setChannelVolume(
                        audioChannel.volume * 100, audioChannel.panorama, audioChannel.inputIndex,
                        audioChannel.outputIndex, (masterChannel?.volume ?: 0) * 100,
                        masterChannel?.panorama ?: 0, mute
                    )
                }
            }
            for (fxSend in scene.fxSends) {
                val audioChannel = scene.channelsByOutputsMap[1]?.get(fxSend.inputIndex - 1)
                val channelVolume = audioChannel?.volume ?: VOLUME_MIN
                val channelMute = audioChannel?.mute ?: false
                usbConnection?.setFxSend(
                    fxSend.volume * 100,
                    fxSend.inputIndex,
                    channelVolume * 100,
                    channelMute
                )
            }
            for (master in scene.masterChannels) {
                usbConnection?.setFxReturn(
                    master.fxReturn * 100,
                    master.outputIndex,
                    master.volume,
                    master.panorama,
                    master.mute
                )
            }
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