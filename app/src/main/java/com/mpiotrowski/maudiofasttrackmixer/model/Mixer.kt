package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Mixer(
    @PrimaryKey val id: Int,
    var output: Int,

    @Embedded
    @ColumnInfo(name = "master_channel")var masterChannel: MasterChannel
)