package com.mpiotrowski.maudiofasttrackmixer.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
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

        val FX_TYPES = arrayOf(
            FxType.ROOM1, FxType.ROOM2,
            FxType.ROOM3, FxType.HALL1,
            FxType.HALL2, FxType.PLATE,
            FxType.DELAY, FxType.ECHO
        )

        val SAMPLE_RATE = arrayOf(
            SampleRate.SR_441, SampleRate.SR_48,
            SampleRate.SR_882, SampleRate.SR_96
        )
    }

    private var selectedFxType = 0
    private var selectedSampleRate = 0
    private var connection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null

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

    private fun setConfiguration(): String {
        return connection?.controlTransfer(
            0x00,//request type
            9,//
            1,//wValue - output/input
            0x0000,//wIndex
            null,//buffer
            0,//length
            USB_TIMEOUT// timeout
        ).toString()
    }

    fun setChannelVolume(volume: Int, pan: Int, input: Int, outputPair: Int, masterVolume: Int, masterPan: Int, mute: Boolean): String {
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
        val bufferLeft = toReversedByteArray(leftLogValue)
        val bufferRight = toReversedByteArray(rightLogValue)
        setVolume(input,outputPair*2 - 1, bufferLeft)
        return setVolume(input,outputPair*2, bufferRight)
    }

    fun setFxSend(sendValue: Int, input: Int) {
        val logValue = toLogScale(sendValue, SEND_MIN, SEND_DELTA, SEND_SCALE)
        val buffer = toReversedByteArray(logValue)
        setVolume(input,9, buffer)
    }

    fun setFxReturn(fxReturnValue: Int, outputPair: Int) {
        val logValue = toLogScale(fxReturnValue, RETURN_MIN, RETURN_DELTA, RETURN_SCALE)
        val buffer = toReversedByteArray(logValue)
        setFxReturnVolume(outputPair*2-1, buffer)
        setFxReturnVolume(outputPair*2, buffer)
    }

    private fun toLogScale(value: Int, scaleStart: Int, scaleDelta: Int, widgetScale: Int): Int {
        return (scaleStart + (kotlin.math.log10((1 + value).toDouble()) / kotlin.math.log10(widgetScale.toDouble()))*scaleDelta).toInt()
    }

    private fun toReversedByteArray(number: Int): ByteArray {
        return byteArrayOf((number and 0x00FF).toByte(), ((number and 0xFF00) shr 8).toByte())
    }

    //wIndex 0x0500 wysyłka na wyjścia(parametr value) 1-8,9(AUX) kanałów  1-16, wartość 0xffff - ?
    private fun setVolume(input: Int, output: Int, value: ByteArray): String {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            output.shl(8).or(input),//output//input 0xffff, 0x (01-09) (01-09,0a-0f,10)
            0x0500,//wIndex
            value,//
            2,//length
            USB_TIMEOUT// timeout
        ).toString()
    }

    private fun setFxParameter(parameterType: Int, parameterValue: ByteArray, valueLength: Int): String {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            parameterType,//output//input 0xffff, 0x (01-09) (01-09,0a-0f,10)
            0x0600,//wIndex
            parameterValue,//
            valueLength,//length
            USB_TIMEOUT// timeout
        ).toString()
    }

    fun setFxReturnVolume(output: Int, value: ByteArray): String {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            2.shl(8).or(output),//output//input 0xffff, 0x (01-09) (01-09,0a-0f,10)
            0x0700,//wIndex
            value,//
            2,//length
            USB_TIMEOUT// timeout
        ).toString()
    }

    fun setSampleRate(value: ByteArray) {
        LogUtil.d("sample rate 1 " + setInternalSampleRate(value))
        LogUtil.d("sample rate 2 " + setDawSampleRate(value))
    }

    fun setInternalSampleRate(value: ByteArray): String{
        return connection?.controlTransfer(
            0x22,//request type
            1,//
            0x0100,
            1,//wIndex
            value,//
            3,//length
            USB_TIMEOUT// timeout
        ).toString()
    }

    fun setDawSampleRate(value: ByteArray): String {
        return connection?.controlTransfer(
            0x22,//request type
            1,//
            0x0100,
            129,//wIndex
            value,//
            3,//length
            USB_TIMEOUT// timeout
        ).toString()
    }

    fun setFxType(): FxType {
        if(selectedFxType + 1 == FX_TYPES.size)
            selectedFxType = 0
        else
            selectedFxType += 1
        setFxParameter(FX_TYPE, FX_TYPES[selectedFxType].buffer, 2)
        return FX_TYPES[selectedFxType]
    }

    fun setSampleRate(): SampleRate {
        if(selectedSampleRate + 1 == SAMPLE_RATE.size)
            selectedSampleRate = 0
        else
            selectedSampleRate += 1
        setSampleRate(SAMPLE_RATE[selectedSampleRate].buffer)
        return SAMPLE_RATE[selectedSampleRate]
    }

    //1..127 linear
    fun setFxVolume(value: Int): String {
        return setFxParameter(FX_VOLUME, byteArrayOf(value.toByte()), 1)
    }

    //1..127 linear
    fun setFxDuration(value: Int): String {
        val byteArray = byteArrayOf(0.toByte(), value.toByte())
        return setFxParameter(FX_DURATION, byteArray, 2)
    }

    //1..127 linear
    fun setFxFeedback(value: Int): String {
        return setFxParameter(FX_FEEDBACK, byteArrayOf(value.toByte()), 1)
    }

    private fun muteAll(): String {
        return connection?.controlTransfer(
            0x21,//request type
            1,//
            0xffff,//wValue - input/output
            0x0500,//wIndex
            toReversedByteArray(32768),
            2,//length
            USB_TIMEOUT// timeout
        ).toString()
    }
}