package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

const val MIXER_STEREO_OUTPUTS_NUMBER = 4

data class SceneWithComponents (
    @Embedded val scene: Scene,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "sceneId",
        associateBy = Junction(FxSend::class)
    )
    val fxSends: List<FxSend>,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "sceneId",
        entity = MasterChannel::class
    )
    val masterChannels: List<MasterChannel>,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "sceneId",
        entity = AudioChannel::class
    )
    val audioChannels: List<AudioChannel>
) {
    val fxSendsMap = fxSends.map{it.channelNumber to it}.toMap()
    val channelsMap = audioChannels.groupBy{it.outputNumber}
    val mastersMap = masterChannels.map{it.outputNumber to it}.toMap()
}