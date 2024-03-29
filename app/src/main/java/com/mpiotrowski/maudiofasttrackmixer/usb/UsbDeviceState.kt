package com.mpiotrowski.maudiofasttrackmixer.usb

import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings

class UsbDeviceState {

    private val postFaderChannelsMap = HashMap<Int, HashMap<Int,Int>>()
    val fxSendsMap = HashMap<Int,Int>()
    val fxReturnsMap = HashMap<Int, Int>()

    var sampleRate: SampleRate? = null

    var fxType: FxSettings.FxType? = null

    var fxDuration: Int? = null
    var fxFeedback: Int? = null
    var fxVolume: Int? = null

    fun sameVolume(input: Int, output: Int, value: Int):Boolean {
        val inputsMap = postFaderChannelsMap[output]
        return if(inputsMap == null) {
            false
        } else {
            if(inputsMap[input] == value) {
                true
            } else {
                inputsMap[input] = value
                false
            }
        }
    }

    fun setVolume(input: Int, output: Int, volume: Int) {
        var inputsMap = postFaderChannelsMap[output]
        return if(inputsMap == null) {
            inputsMap = HashMap()
            inputsMap[input] = volume
            postFaderChannelsMap[output] = inputsMap
        } else {
            inputsMap[input] = volume
        }
    }

    fun sameFxReturn(output: Int, value: Int):Boolean {
        return fxReturnsMap[output] == value
    }


    fun sameSampleRate(sampleRate: SampleRate):Boolean {
        return this.sampleRate == sampleRate
    }

    fun sameFxType(fxType: FxSettings.FxType):Boolean {
        return this.fxType == fxType
    }

}