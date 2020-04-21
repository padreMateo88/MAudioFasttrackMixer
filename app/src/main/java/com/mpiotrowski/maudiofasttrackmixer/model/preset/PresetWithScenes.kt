package com.mpiotrowski.maudiofasttrackmixer.model.preset

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.Scene

data class PresetWithScenes (
    @Embedded val preset: Preset,
    @Relation(
            parentColumn = "presetId",
            entityColumn = "sceneId",
            associateBy = Junction(PresetSceneCrossRef::class)
        )
        val scenes: List<Scene>
)