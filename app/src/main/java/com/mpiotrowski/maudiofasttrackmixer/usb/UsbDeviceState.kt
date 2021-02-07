package com.mpiotrowski.maudiofasttrackmixer.usb

class UsbDeviceState {

    val outputsMap = HashMap<Int, HashMap<Int,Int>>()
    val fxSendsMap = HashMap<Int,Int>()
    val fxReturnsMap = HashMap<Int, Int>()

    var sampleRate: SampleRate? = null

    var fxType: FxType? = null

    var fxDuration: Int? = null
    var fxFeedback: Int? = null
    var fxVolume: Int? = null

    fun sameVolume(input: Int, output: Int, value: Int):Boolean {
        val inputsMap = outputsMap[output]
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

    fun setVolume(input: Int, output: Int, value: Int) {
        var inputsMap = outputsMap[output]
        return if(inputsMap == null) {
            inputsMap = HashMap()
            inputsMap[input] = value
            outputsMap[output] = inputsMap
        } else {
                inputsMap[input] = value
        }
    }

    fun sameFxSend(input: Int, value: Int):Boolean {
        return fxSendsMap[input] == value
    }

    fun sameFxReturn(output: Int, value: Int):Boolean {
        return fxReturnsMap[output] == value
    }


    fun sameSampleRate(sampleRate: SampleRate):Boolean {
        return this.sampleRate == sampleRate
    }

    fun sameFxType(fxType: FxType):Boolean {
        return this.fxType == fxType
    }

    fun sameFxDuration(fxDuration: Int):Boolean {
        return this.fxDuration == fxDuration
    }

    fun sameFxFeedback(fxFeedback: Int):Boolean {
        return this.fxFeedback == fxFeedback
    }

    fun sameFxVolume(fxVolume: Int):Boolean {
        return this.fxVolume == fxVolume
    }

}