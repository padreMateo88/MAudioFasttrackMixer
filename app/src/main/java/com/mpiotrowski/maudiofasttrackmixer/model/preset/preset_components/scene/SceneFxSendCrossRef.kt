package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene

import androidx.room.Entity

@Entity(primaryKeys = ["sceneId", "fxSendId"])
class SceneFxSendCrossRef (
    val sceneId: Long,
    val fxSendId: Long
)