package com.mpiotrowski.maudiofasttrackmixer.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import kotlin.math.abs

class UsbConnectionHelper {

    companion object {
        private const val MASTER_VOLUME_SCALE = 10000

        private const val VOLUME_SCALE = 10000
        private const val VOLUME_MIN = 32768
        private const val VOLUME_DELTA = 31997

        private const val PAN_MAX = 100

        private const val SEND_SCALE = 10000
        private const val SEND_MIN = 52711
        private const val SEND_DELTA = 12787

        private const val RETURN_SCALE = 10000
        private const val RETURN_MIN = 52711
        private const val RETURN_DELTA = 12787

        private const val FX_TYPE = 0x0100
        private const val FX_VOLUME = 0x0200
        private const val FX_DURATION = 0x0300
        private const val FX_FEEDBACK = 0x0400

        private const val USB_TIMEOUT = 1000
    }

    private var connection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var usbDeviceState: UsbDeviceState? = null

    fun connectDevice(context: Context, device: UsbDevice) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        usbInterface = device.getInterface(0).also { intf ->
            connection = usbManager.openDevice(device)
            try{
                val claimed = connection?.claimInterface(intf,true) ?: false
                if (connection != null && claimed) {
                    LogUtil.d("USB CONNECTED")
                    val conf = setConfiguration()
                    muteAll()
                    usbDeviceState = UsbDeviceState()
                    LogUtil.d("USB CONFIGURATION $conf")
                } else {
                    LogUtil.d("USB NOT CONNECTED")
                }

            }catch (e: Exception){
                LogUtil.d("USB EXCEPTION ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun disconnectDevice() {
        connection?.releaseInterface(usbInterface)
        connection?.close()
    }

    private fun setConfiguration(): Int {
        return connection?.controlTransfer(
            0x00,//request type
            9,//
            1,//wValue - output/input
            0x0000,//wIndex
            null,//buffer
            0,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

//region public audio mixer parameter setters
    fun setChannelVolume(volume: Int, pan: Int, input: Int, outputPair: Int, masterVolume: Int, masterPan: Int, mute: Boolean) {
        val isPanRight = pan > 0
        val panCoefficient = (PAN_MAX.toDouble() -  abs(pan))/ PAN_MAX
        val leftPanCoefficient = if(isPanRight) panCoefficient else 1.toDouble()
        val rightPanCoefficient = if(!isPanRight) panCoefficient else 1.toDouble()

        val isMasterPanRight = masterPan > 0
        val masterPanCoefficient = (PAN_MAX.toDouble() -  abs(masterPan))/ PAN_MAX
        val leftMasterPanCoefficient = if(isMasterPanRight) masterPanCoefficient else 1.toDouble()
        val rightMasterPanCoefficient = if(!isMasterPanRight) masterPanCoefficient else 1.toDouble()
        val masterAppliedVolume =  volume.toDouble()*masterVolume/ MASTER_VOLUME_SCALE
        var leftLogValue = toLogScale((masterAppliedVolume*leftPanCoefficient*leftMasterPanCoefficient).toInt(),
            VOLUME_MIN, VOLUME_DELTA, VOLUME_SCALE
        )
        var rightLogValue = toLogScale((masterAppliedVolume*rightPanCoefficient*rightMasterPanCoefficient).toInt(),
            VOLUME_MIN, VOLUME_DELTA, VOLUME_SCALE
        )
        if(mute) {
            leftLogValue = VOLUME_MIN
            rightLogValue = VOLUME_MIN
        }
        setVolumeRegardDeviceState(input,outputPair*2 - 1, leftLogValue)
        setVolumeRegardDeviceState(input,outputPair*2, rightLogValue)
         //TODO
        //setFxSend((usbDeviceState?.fxSendsMap?.get(input) ?: SEND_MIN ), input)
    }

    fun setFxSend(sendValue: Int, input: Int) {
        //TODO
//        val channelVolume = usbDeviceState?.outputsMap?.get(1)?.get(input)
//        val scaledChannelVolume = (channelVolume ?: VOLUME_MIN) - VOLUME_MIN
//        val channelAppliedSend = sendValue*scaledChannelVolume/VOLUME_DELTA
//        val logValue = toLogScale(channelAppliedSend, SEND_MIN, SEND_DELTA, SEND_SCALE)
//        Log.d("MPdebug", "logValue $logValue channelVolume $channelVolume scaledChannelVolume: $scaledChannelVolume sendValue $sendValue channelAppliedSend $channelAppliedSend")

        val logValue = toLogScale(sendValue, SEND_MIN, SEND_DELTA, SEND_SCALE)
        val buffer = toReversedByteArray(logValue)
        if(setVolume(input, 9, buffer) >= 0){
            usbDeviceState?.fxSendsMap?.put(input, sendValue)
        }
    }

    fun setFxReturn(fxReturnValue: Int, outputPair: Int) {
        //TODO
//        if (usbDeviceState?.sameFxReturn(outputPair,fxReturnValue)?.not() == true) {
//            val logValue = toLogScale(fxReturnValue, RETURN_MIN, RETURN_DELTA, RETURN_SCALE)
//            val buffer = toReversedByteArray(logValue)
//            if (setFxReturnVolume(outputPair*2-1, buffer) >= 0
//                && setFxReturnVolume(outputPair*2, buffer) >= 0) {
//                usbDeviceState?.fxReturnsMap?.put(outputPair,fxReturnValue)
//            }
//        }
    }

    fun setFxType(fxType: FxSettings.FxType) {
        if(usbDeviceState?.sameFxType(fxType)?.not() == true) {
            if(setFxParameter(FX_TYPE, fxType.getBuffer(), 2) >= 0) {
                usbDeviceState?.fxType = fxType
            }
        }
    }

    //1..127 linear
    fun setFxVolume(value: Int) {
        if (usbDeviceState?.sameFxVolume(value)?.not() == true) {
            if(setFxParameter(FX_VOLUME, byteArrayOf(value.toByte()), 1) >= 0 ) {
                usbDeviceState?.fxVolume = value
            }
        }
    }

    //1..127 linear
    fun setFxDuration(value: Int) {
        if(usbDeviceState?.sameFxDuration(value)?.not() == true) {
            Log.d("MPdebug", "DURATION $value")
            val byteArray = byteArrayOf(0.toByte(), value.toByte())
            if(setFxParameter(FX_DURATION, byteArray, 2) >= 0) {
                usbDeviceState?.fxDuration = value
            }
        }
    }

    //1..127 linear
    fun setFxFeedback(value: Int) {
        if(usbDeviceState?.sameFxFeedback(value)?.not() == true) {
            if(setFxParameter(FX_FEEDBACK, byteArrayOf(value.toByte()), 1) >= 0) {
                usbDeviceState?.fxFeedback = value
            }
        }
    }

    fun setSampleRate(sampleRate: SampleRate) {
        if(usbDeviceState?.sameSampleRate(sampleRate)?.not() == true) {
            if(setSampleRate(sampleRate.buffer) >= 0) {
                usbDeviceState?.sampleRate = sampleRate
            }
        }
    }

//endregion public audio mixer parameter setters

//region private low level USB calls

    private fun setVolumeRegardDeviceState(input: Int, output: Int, value: Int) {
        if(usbDeviceState?.sameVolume(input, output, value)?.not() == true) {
            val buffer = toReversedByteArray(value)
            if(setVolume(input, output, buffer) >= 0) {
                usbDeviceState?.setVolume(input, output, value)
            }
        }
    }

    //wIndex 0x0500 wysyłka na wyjścia(parametr value) 1-8,9(AUX) kanałów  1-16, wartość 0xffff - ?
    private fun setVolume(input: Int, output: Int, value: ByteArray): Int {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            output.shl(8).or(input),//output//input 0xffff, 0x (01-09) (01-09,0a-0f,10)
            0x0500,//wIndex
            value,//
            2,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

    private fun setFxReturnVolume(output: Int, value: ByteArray): Int {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            2.shl(8).or(output),//output//input 0xffff, 0x (01-09) (01-09,0a-0f,10)
            0x0700,//wIndex
            value,//
            2,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

    private fun setFxParameter(parameterType: Int, parameterValue: ByteArray, valueLength: Int): Int {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            parameterType,//output//input 0xffff, 0x (01-09) (01-09,0a-0f,10)
            0x0600,//wIndex
            parameterValue,//
            valueLength,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

    private fun setSampleRate(value: ByteArray): Int {
        return if(setInternalSampleRate(value) >= 0 && setDawSampleRate(value) >=0)
            0
        else
            -1
    }

    private fun setInternalSampleRate(value: ByteArray): Int{
        return connection?.controlTransfer(
            0x22,//request type
            1,//
            0x0100,
            1,//wIndex
            value,//
            3,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

    private fun setDawSampleRate(value: ByteArray): Int {
        return connection?.controlTransfer(
            0x22,//request type
            1,//
            0x0100,
            129,//wIndex
            value,//
            3,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

    private fun muteAll(): Int {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            0xffff,//wValue - input/output
            0x0500,//wIndex
            toReversedByteArray(32768),
            2,//length
            USB_TIMEOUT// timeout
        ) ?: -1
    }

//endregion private low level USB calls

    private fun toLogScale(value: Int, scaleStart: Int, scaleDelta: Int, widgetScale: Int): Int {
        return (scaleStart + (kotlin.math.log10((1 + value).toDouble()) / kotlin.math.log10(widgetScale.toDouble()))*scaleDelta).toInt()
    }

    private fun toReversedByteArray(number: Int): ByteArray {
        return byteArrayOf((number and 0x00FF).toByte(), ((number and 0xFF00) shr 8).toByte())
    }
}