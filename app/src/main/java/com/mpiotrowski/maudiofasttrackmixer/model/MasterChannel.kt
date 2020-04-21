package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MasterChannel (
    @PrimaryKey var id: Int,
    var volume : Int = 75,
    @ColumnInfo(name = "fx_return") var fxReturn : Int = 0,
    var mute: Boolean = false
)