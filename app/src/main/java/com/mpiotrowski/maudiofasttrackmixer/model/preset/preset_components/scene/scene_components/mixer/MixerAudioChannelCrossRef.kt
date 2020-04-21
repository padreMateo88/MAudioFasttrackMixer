package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer

import androidx.room.Entity

@Entity(primaryKeys = ["mixerId", "audioChannelId"])
data class MixerAudioChannelCrossRef(
    val mixerId: Long,
    val audioChannel: Long
)
