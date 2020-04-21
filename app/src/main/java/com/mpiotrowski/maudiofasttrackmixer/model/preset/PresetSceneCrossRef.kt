package com.mpiotrowski.maudiofasttrackmixer.model.preset

import androidx.room.Entity

@Entity(primaryKeys = ["presetId", "sceneId"])
data class PresetSceneCrossRef(
    val presetId: Long,
    val sceneId: Long
)
