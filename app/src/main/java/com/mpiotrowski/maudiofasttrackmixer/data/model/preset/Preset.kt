package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import java.util.*

const val LAST_PERSISTED_STATE_ID = "LAST_PERSISTED_STATE"
const val LAST_PERSISTED_STATE_NAME = ""

const val DEFAULT_PRESET_ID = "DEFAULT_PRESET"
const val DEFAULT_PRESET_NAME = "Default preset"

@Entity
data class Preset(
    @PrimaryKey var presetId: String = UUID.randomUUID().toString(),
    var presetName: String,
    var sampleRate: SampleRate = SampleRate.SR_48
)