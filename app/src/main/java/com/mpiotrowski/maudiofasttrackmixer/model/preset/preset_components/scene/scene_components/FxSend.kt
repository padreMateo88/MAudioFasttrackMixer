package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FxSend(@PrimaryKey var fxSendId: Long = Long.MIN_VALUE,
                  var channelId : Int = 0,
                  var volume : Int = 0
)