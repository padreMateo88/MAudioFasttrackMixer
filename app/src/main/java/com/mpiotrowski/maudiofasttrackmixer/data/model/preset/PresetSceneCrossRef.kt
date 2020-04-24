package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Entity

@Entity(primaryKeys = ["presetId", "sceneId"])
data class PresetSceneCrossRef(
    val presetId: String,
    val sceneId: Long
)
