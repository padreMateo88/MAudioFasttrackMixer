package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import androidx.room.Embedded
import androidx.room.Ignore
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
) {
    @Ignore
    val scenesByOrder = scenes.map{it.scene.sceneOrder to it}.toMap()

    fun copyValues(copyFrom: PresetWithScenes, presetName: String) {
        this.preset.presetName = presetName
        this.preset.sampleRate = this.preset.sampleRate
        for(order in scenesByOrder.keys) {
            copyFrom.scenesByOrder[order]?.let {
                scenesByOrder[order]?.copyValues(it,it.scene.sceneOrder,it.scene.sceneName)
            }
        }
    }
}