package com.mpiotrowski.maudiofasttrackmixer.usb

class UsbDeviceState {

    private val outputsMap = HashMap<Int, HashMap<Int,Int>>()
    private val fxSendsMap = HashMap<Int,Int>()
    private val fxReturnsMap = HashMap<Int, Int>()

    private var sampleRate: SampleRate? = null

    private var fxType: FxType? = null

    private var fxDuration: Int? = null
    private var fxFeedback: Int? = null
    private var fxVolume: Int? = null

    fun sameLevel(input: Int, output: Int, value: Int):Boolean {
        var inputsMap = outputsMap[output]
        return if(inputsMap == null) {
            inputsMap = HashMap()
            inputsMap[input] = value
            outputsMap[output] = inputsMap
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

    fun sameFxSend(input: Int, value: Int):Boolean {
        return if(fxSendsMap[input] == value)
            true
        else{
            fxSendsMap[input] = value
            false
        }
    }

    fun sameFxReturn(output: Int, value: Int):Boolean {
        return if(fxReturnsMap[output] == value)
            true
        else{
            fxReturnsMap[output] = value
            false
        }
    }


    fun sameSampleRate(sampleRate: SampleRate):Boolean {
        return if(this.sampleRate == sampleRate)
            true
        else{
            this.sampleRate = sampleRate
            false
        }
    }

    fun sameFxType(fxType: FxType):Boolean {
        return if(this.fxType == fxType)
            true
        else{
            this.fxType = fxType
            false
        }
    }

    fun sameFxDuration(fxDuration: Int):Boolean {
        return if(this.fxDuration == fxDuration)
            true
        else{
            this.fxDuration = fxDuration
            false
        }
    }

    fun sameFxFeedback(fxFeedback: Int):Boolean {
        return if(this.fxFeedback == fxFeedback)
            true
        else{
            this.fxFeedback = fxFeedback
            false
        }
    }

    fun sameFxVolume(fxVolume: Int):Boolean {
        return if(this.fxVolume == fxVolume)
            true
        else{
            this.fxVolume = fxVolume
            false
        }
    }

}