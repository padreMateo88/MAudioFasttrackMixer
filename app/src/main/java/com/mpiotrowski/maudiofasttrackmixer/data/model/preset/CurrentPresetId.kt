package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.*

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
    @PrimaryKey val id: Long = Long.MAX_VALUE,
    var presetId: String
)