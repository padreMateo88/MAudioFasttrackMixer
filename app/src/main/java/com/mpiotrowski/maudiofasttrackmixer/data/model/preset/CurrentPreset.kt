package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.*

const val CURRENT_PRESET_ID = Long.MAX_VALUE

@Entity
data class CurrentPreset (
    @PrimaryKey var id: Long = CURRENT_PRESET_ID,
    var presetId: String
)