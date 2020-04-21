package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer.mixer_components.AudioChannel

data class MixerWithAudioChannels (
    @Embedded val mixer: Mixer,
    @Relation(
        parentColumn = "mixerId",
        entityColumn = "audioChannelId",
        associateBy = Junction(MixerAudioChannelCrossRef::class)
    )
    val audioChannels: List<AudioChannel>
)