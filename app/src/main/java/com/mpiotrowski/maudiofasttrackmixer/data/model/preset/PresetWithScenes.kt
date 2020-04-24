package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents

data class PresetWithScenes (
    @Embedded val preset: Preset,
    @Relation(
            parentColumn = "presetId",
            entityColumn = "sceneId",
            associateBy = Junction(PresetSceneCrossRef::class)
    )
    val scenes: List<SceneWithComponents>
)