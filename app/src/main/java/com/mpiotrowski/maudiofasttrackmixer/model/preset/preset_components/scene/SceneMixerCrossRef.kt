package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene

import androidx.room.Entity

@Entity(primaryKeys = ["sceneId", "mixerId"])
data class SceneMixerCrossRef(
    val sceneId: Long,
    val mixerId: Long
)