package com.mpiotrowski.maudiofasttrackmixer.usb

class ByteUtil {
    companion object{
        fun toByteArray(number: Int): ByteArray {
            var restrictedNumber = number
            return byteArrayOf(((restrictedNumber and 0xFF00) shr 8).toByte(), (restrictedNumber and 0x00FF).toByte())
        }
    }
}