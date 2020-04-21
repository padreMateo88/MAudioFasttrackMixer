package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioChannel(
    @PrimaryKey var id: Int,
    var volume : Int = 75,
    @ColumnInfo(name = "channel_number")var channelNumber : Int = 0,
    var panorama: Int = 0,
    var mute: Boolean = false,
    var solo: Boolean = false
)