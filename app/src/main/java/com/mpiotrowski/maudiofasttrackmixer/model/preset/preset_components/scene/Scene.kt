package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.FxSettings

@Entity
data class Scene(
    @PrimaryKey var sceneId: Long,
    var sceneName : String,
    @Embedded
    var fxSettings: FxSettings
)