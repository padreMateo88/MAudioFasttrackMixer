package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components

import androidx.room.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene

@Entity(
    foreignKeys = [ForeignKey(
        entity = Scene::class,
        parentColumns = arrayOf("sceneId"),
        childColumns = arrayOf("sceneId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["sceneId"]), Index(value = ["fxSendId"], unique = true)]
)
data class FxSend(
    @PrimaryKey(autoGenerate = true) var fxSendId: Long = 0,
    var sceneId: Long = 0,
    var inputIndex : Int,
    var volume : Int = 0
) {
    @Ignore
    var isDirty = false
}