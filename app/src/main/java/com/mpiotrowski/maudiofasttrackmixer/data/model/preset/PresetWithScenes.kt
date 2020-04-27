package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Embedded
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents

const val SCENES_IN_PRESET_NUMBER = 8
data class PresetWithScenes (
    @Embedded val preset: Preset,
    @Relation(
        parentColumn = "presetId",
        entityColumn = "presetId",
        entity = Scene::class
    )
    val scenes: List<SceneWithComponents>
)