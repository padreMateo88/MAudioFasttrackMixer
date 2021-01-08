package com.mpiotrowski.maudiofasttrackmixer.usb

import com.mpiotrowski.maudiofasttrackmixer.usb.ByteUtil

enum class FxType(val buffer: ByteArray) {
    ROOM1(ByteUtil.toByteArray(0x0000)),
    ROOM2(ByteUtil.toByteArray(0x0100)),
    ROOM3(ByteUtil.toByteArray(0x0200)),
    HALL1(ByteUtil.toByteArray(0x0300)),
    HALL2(ByteUtil.toByteArray(0x0400)),
    PLATE(ByteUtil.toByteArray(0x0500)),
    DELAY(ByteUtil.toByteArray(0x0600)),
    ECHO (ByteUtil.toByteArray(0x0700))
}