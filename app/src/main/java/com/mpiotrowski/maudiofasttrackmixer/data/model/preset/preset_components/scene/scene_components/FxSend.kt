package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    @PrimaryKey var fxSendId: Long = 0,
    var sceneId: Long,
    var channelNumber : Int = 0,
    var volume : Int = 0
)