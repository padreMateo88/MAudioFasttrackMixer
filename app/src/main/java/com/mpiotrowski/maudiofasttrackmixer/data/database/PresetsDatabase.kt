package com.mpiotrowski.maudiofasttrackmixer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Preset::class,
        Scene::class,
        MasterChannel::class,
        AudioChannel::class,
        FxSend::class,
        CurrentPreset::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PresetsDatabase : RoomDatabase() {

    abstract fun presetsDao(): PresetsDao

    companion object {
        @Volatile
        private var INSTANCE: PresetsDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PresetsDatabase {
            return INSTANCE
                ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    PresetsDatabase::class.java,
                    "presets_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(PresetsDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class PresetsDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.presetsDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(presetsDao: PresetsDao) {
            val defaultPreset = presetsDao.getPreset(DEFAULT_PRESET_ID)
            if(defaultPreset.isEmpty()) {
                presetsDao.addPreset(Preset(presetId = DEFAULT_PRESET_ID, presetName = DEFAULT_PRESET_NAME))
            }

            val currentState = presetsDao.getPreset(LAST_PERSISTED_STATE_ID)
            if(currentState.isEmpty()) {
                presetsDao.addPreset(Preset(presetId = LAST_PERSISTED_STATE_ID, presetName = LAST_PERSISTED_STATE_NAME))
            }

            val currentPreset = presetsDao.getCurrentPresetId()
            if(currentPreset.isEmpty()) {
                presetsDao.insertCurrentPreset(CurrentPreset(presetId = DEFAULT_PRESET_ID))
            }
        }
    }
}
