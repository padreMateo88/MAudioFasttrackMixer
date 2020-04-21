package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FxSettings(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "fx_type")var fxType: MutableLiveData<FxType> = MutableLiveData(),
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
}