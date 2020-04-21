package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Preset(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "preset_name")var presetName: String,
    @ColumnInfo(name = "sample_rate")var sampleRate: SampleRate
)