package com.mpiotrowski.maudiofasttrackmixer.usb

class ByteUtil {
    companion object{
        fun toByteArray(number: Int): ByteArray {
            return byteArrayOf(((number and 0xFF00) shr 8).toByte(), (number and 0x00FF).toByte())
        }
    }
}