package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import androidx.room.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings

@Entity(
    foreignKeys = [ForeignKey(
        entity = Preset::class,
        parentColumns = arrayOf("presetId"),
        childColumns = arrayOf("presetId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["sceneId"]), Index(value = ["fxSendId"], unique = true)]
)
data class Scene(
    var sceneName : String = "",
    var presetId : String = "",
    @Embedded var fxSettings: FxSettings,
    @PrimaryKey(autoGenerate = true) var sceneId: Long = 0
)