package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components

import androidx.lifecycle.MutableLiveData
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverter

@Entity
data class FxSettings(
    @Embedded var fxType: MutableLiveData<FxType> = MutableLiveData(),
    var duration: MutableLiveData<Int> = MutableLiveData(),
    var feedback: MutableLiveData<Int> = MutableLiveData(),
    var volume: MutableLiveData<Int> = MutableLiveData()
) {
    init {
        fxType.value = FxType.ROOM1
        duration.value = 0
        feedback.value = 0
        volume.value = 0
    }

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
    }
}