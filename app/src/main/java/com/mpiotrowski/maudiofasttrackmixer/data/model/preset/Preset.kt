package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import java.util.*

@Entity
data class Preset(
    @PrimaryKey var presetId: String = UUID.randomUUID().toString(),
    var presetName: String,
    @Embedded var sampleRate: SampleRate

)