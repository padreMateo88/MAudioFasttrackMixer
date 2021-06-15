package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components

import androidx.room.Entity
import androidx.room.Ignore
import com.mpiotrowski.maudiofasttrackmixer.usb.ByteUtil

@Entity
data class FxSettings(
    var fxType: FxType = FxType.ROOM1,
    var duration: Int = 0,
    var feedback: Int = 0,
    var volume: Int = 0
) {
    @Ignore
    var isDirty = false

    enum class FxType(var apiCode: Int,var fxName: String) {
        ROOM1(0x0000, "Room 1"),
        ROOM2(0x0100, "Room 2"),
        ROOM3(0x0200, "Room 3"),
        HALL1(0x0300, "Hall 1"),
        HALL2(0x0400, "Hall 2"),
        PLATE(0x0500, "Plate"),
        DELAY(0x0600, "Delay"),
        ECHO(0x0700, "Echo");

        override fun toString(): String {
            return fxName
        }

        fun getBuffer(): ByteArray{
            return ByteUtil.toByteArray(apiCode)
        }
    }
}