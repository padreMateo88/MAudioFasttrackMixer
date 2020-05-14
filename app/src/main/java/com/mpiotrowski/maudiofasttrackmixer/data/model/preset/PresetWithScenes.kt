package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents

const val SCENES_IN_PRESET_COUNT = 9
data class PresetWithScenes (
    @Embedded val preset: Preset,
    @Relation(
        parentColumn = "presetId",
        entityColumn = "presetId",
        entity = Scene::class
    )
    val scenes: List<SceneWithComponents>
) {
    @Ignore
    val scenesByOrder = scenes.map {it.scene.sceneOrder to it}.toMap().toMutableMap()

    fun copyValues(copyFrom: PresetWithScenes, presetName: String) {
        this.preset.presetName = presetName
        this.preset.sampleRate = copyFrom.preset.sampleRate
        this.preset.isDirty = copyFrom.preset.isDirty
        for(order in scenesByOrder.keys) {
            copyFrom.scenesByOrder[order]?.let {
                scenesByOrder[order]?.copyValues(it,it.scene.sceneName)
            }
        }
    }

    companion object {
        fun newInstance(preset: Preset): PresetWithScenes {
            val scenes = mutableListOf<SceneWithComponents>()
            for(index in 1 .. SCENES_IN_PRESET_COUNT) {
                scenes.add(SceneWithComponents.newInstance("Scene $index", preset.presetId, index))
            }
            return PresetWithScenes(preset,scenes)
        }
    }
}