package com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.model.preset.preset_components.scene.scene_components.mixer.Mixer

data class SceneWithMixersAndFxSends (
    @Embedded val scene: Scene,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "fxSendId",
        associateBy = Junction(SceneFxSendCrossRef::class)
    )
    val fxSends: List<FxSend>,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "mixerId",
        associateBy = Junction(SceneMixerCrossRef::class)
    )
    val mixers: List<Mixer>
)