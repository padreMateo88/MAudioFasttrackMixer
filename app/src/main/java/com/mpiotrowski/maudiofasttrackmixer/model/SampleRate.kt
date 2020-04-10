package com.mpiotrowski.maudiofasttrackmixer.model

enum class SampleRate(sampleRate: Int, name: String) {
    SR_44_1(44100, "44.1 kHz"),
    SR_48(48000, "48 kHz"),
    SR_88_2(88200, "88.2 kHz"),
    SR_96(96000, "96 kHz")
}