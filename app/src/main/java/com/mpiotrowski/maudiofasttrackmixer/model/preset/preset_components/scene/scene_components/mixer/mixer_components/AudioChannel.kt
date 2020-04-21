package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer.mixer_components

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AudioChannel(
    @PrimaryKey(autoGenerate = true) var audioChannelId: Long = Long.MIN_VALUE,
    var volume : Int = 75,
    var channelNumber : Int = 0,
    var panorama: Int = 0,
    var mute: Boolean = false,
    var solo: Boolean = false
)