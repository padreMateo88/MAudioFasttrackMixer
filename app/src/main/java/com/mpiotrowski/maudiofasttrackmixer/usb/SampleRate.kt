package com.mpiotrowski.maudiofasttrackmixer.usb

enum class SampleRate(val buffer: ByteArray) {
    SR_441(byteArrayOf(0x44.toByte(),0xac.toByte(), 0x00.toByte())),
    SR_48(byteArrayOf(0x80.toByte(),0xbb.toByte(), 0x00.toByte())),
    SR_882(byteArrayOf(0x88.toByte(),0x58.toByte(), 0x01.toByte())),
    SR_96(byteArrayOf(0x00.toByte(),0x77.toByte(), 0x01.toByte()))
}