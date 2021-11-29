package com.mpiotrowski.maudiofasttrackmixer.usb

class ByteUtil {
    companion object{
        fun toByteArray(number: Int): ByteArray {
            var restrictedNumber = number

            if(restrictedNumber > 65535)
                restrictedNumber = 65535
            else if (restrictedNumber < 0)
                restrictedNumber = 0

            return byteArrayOf(((restrictedNumber and 0xFF00) shr 8).toByte(), (restrictedNumber and 0x00FF).toByte())
        }
    }
}