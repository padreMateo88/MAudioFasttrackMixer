package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components

enum class SampleRate(var frequencyValue: Int, var frequencyName: String, var buffer: ByteArray) {
    SR_44_1(44100, "44.1 kHz", byteArrayOf(0x44.toByte(),0xac.toByte(), 0x00.toByte())),
    SR_48(48000, "48 kHz", byteArrayOf(0x80.toByte(),0xbb.toByte(), 0x00.toByte())),
    SR_88_2(88200, "88.2 kHz", byteArrayOf(0x88.toByte(),0x58.toByte(), 0x01.toByte())),
    SR_96(96000, "96 kHz", byteArrayOf(0x00.toByte(),0x77.toByte(), 0x01.toByte()));
    
    override fun toString(): String {
        return frequencyName
    }
}