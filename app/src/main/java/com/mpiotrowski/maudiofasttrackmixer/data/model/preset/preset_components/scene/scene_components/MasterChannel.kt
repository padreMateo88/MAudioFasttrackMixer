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
    indices = [Index(value = ["sceneId"]), Index(value = ["masterId"], unique = true)]
    )
data class MasterChannel (
    @PrimaryKey(autoGenerate = true) var masterId: Long = 0,
    var sceneId: Long = 0,
    var outputNumber: Int = 0,
    var volume : Int = 75,
    var fxReturn : Int = 0,
    var panorama: Int = 0,
    var mute: Boolean = false
)