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
    indices = [Index(value = ["presetId"]), Index(value = ["sceneId"], unique = true)]
)
data class Scene(
    @PrimaryKey(autoGenerate = true) var sceneId: Long = 0,
    var sceneName: String,
    var sceneOrder: Int,
    var presetId: String = "",
    @Embedded var fxSettings: FxSettings = FxSettings()
)