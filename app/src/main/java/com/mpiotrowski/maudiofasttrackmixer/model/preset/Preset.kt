package com.mpiotrowski.maudiofasttrackmixer.model.preset

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.SampleRate

@Entity
data class Preset(var presetName: String,
    @Embedded
    var sampleRate: SampleRate
) {
    @PrimaryKey(autoGenerate = true) var presetId: Long = 0
}