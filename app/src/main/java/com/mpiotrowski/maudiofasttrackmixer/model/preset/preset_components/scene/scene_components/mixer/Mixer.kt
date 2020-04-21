package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer.mixer_components.MasterChannel

@Entity
data class Mixer(
    @PrimaryKey val mixerId: Long,
    var output: Int,

    @Embedded
    var masterChannel: MasterChannel
)