package com.mpiotrowski.maudiofasttrackmixer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

@Database(
    entities = [
        Preset::class,
        Scene::class,
        MasterChannel::class,
        AudioChannel::class,
        FxSend::class,
        CurrentPreset::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PresetsDatabase : RoomDatabase() {

    abstract fun presetsDao(): PresetsDao

    suspend fun populateDatabase() {
        val presetsDao = presetsDao()
        val defaultPreset = presetsDao.getPreset(DEFAULT_PRESET_ID)
        if(defaultPreset == null) {
            presetsDao.addPreset(Preset(presetId = DEFAULT_PRESET_ID, presetName = DEFAULT_PRESET_NAME))
        }

        val currentState = presetsDao.getPreset(LAST_PERSISTED_STATE_ID)
        if(currentState == null) {
            presetsDao.addPreset(Preset(presetId = LAST_PERSISTED_STATE_ID, presetName = LAST_PERSISTED_STATE_NAME))
        }

        val currentPreset = presetsDao.getCurrentPreset()
        if(currentPreset == null) {
            presetsDao.insertCurrentPreset(CurrentPreset(presetId = DEFAULT_PRESET_ID))
        }
    }
}
