package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings

@Entity
data class Scene(
    var sceneName : String,
    @Embedded var fxSettings: FxSettings,
    @PrimaryKey(autoGenerate = true) var sceneId: Long = 0
)