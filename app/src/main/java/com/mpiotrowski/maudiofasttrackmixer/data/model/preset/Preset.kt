package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import java.util.*

const val CURRENT_PRESET_ID = "CURRENT_PRESET"
const val CURRENT_PRESET_NAME = "Default preset"

@Entity
data class Preset(
    @PrimaryKey var presetId: String = UUID.randomUUID().toString(),
    var presetName: String,
    var sampleRate: SampleRate = SampleRate.SR_48
)