package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.*

const val CURRENT_PRESET_ID = 0

@Entity(
    foreignKeys = [ForeignKey(
        entity = Preset::class,
        parentColumns = arrayOf("presetId"),
        childColumns = arrayOf("presetId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["presetId"]), Index(value = ["id"], unique = true)]
)
data class CurrentPreset (
    @PrimaryKey val id: Int = CURRENT_PRESET_ID,
    var presetId: String
)