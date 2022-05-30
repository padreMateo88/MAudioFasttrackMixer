package com.mpiotrowski.maudiofasttrackmixer.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

@Dao
interface PresetsDao {

//region select
    @Transaction
    @Query("SELECT * FROM Preset WHERE presetId != \"$LAST_PERSISTED_STATE_ID\"")
    fun getPresetsWithScenes(): LiveData<List<PresetWithScenes>>

    @Transaction
    @Query("SELECT * FROM Preset WHERE presetId = :presetId")
    fun getPreset(presetId: String): PresetWithScenes?

    @Query("SELECT * FROM CurrentPreset WHERE id = $CURRENT_PRESET_ID")
    suspend fun getCurrentPreset(): CurrentPreset?

    @Query("SELECT * FROM Preset WHERE presetId in (SELECT presetId FROM CurrentPreset WHERE id = $CURRENT_PRESET_ID)")
    suspend fun getCurrentPresetInstance(): PresetWithScenes

    @Query("SELECT * FROM CurrentPreset")
    fun getCurrentPresetLiveData(): LiveData<List<CurrentPreset>>

    @Transaction
    @Query("SELECT * FROM Preset WHERE presetId = \"$LAST_PERSISTED_STATE_ID\"")
    fun getPersistedState(): LiveData<PresetWithScenes>

    @Query("SELECT * FROM Preset WHERE presetId = \"$LAST_PERSISTED_STATE_ID\"")
    suspend fun getPersistedStateBlocking(): PresetWithScenes?

    @Query("SELECT * FROM Preset WHERE presetId = \"$DEFAULT_PRESET_ID\"")
    suspend fun getDefaultPresetBlocking(): PresetWithScenes?

//endregion select

//region insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentPreset(currentPreset: CurrentPreset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: Preset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: Scene): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterChannel(masterChannel: MasterChannel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioChannel(audioChannel: AudioChannel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFxSend(fxSend: FxSend): Long

    @Transaction
    suspend fun insertPresetWithScenes(presetWithScenes: PresetWithScenes) {
        insertPreset(presetWithScenes.preset)
        for(sceneWithComponents in presetWithScenes.scenes) {
            val sceneId = insertScene(sceneWithComponents.scene)
            sceneWithComponents.scene.sceneId = sceneId
            insertMasterChannelWithSceneId(*sceneWithComponents.masterChannels.toTypedArray(), sceneId = sceneId)
            insertAudioChannelWithSceneId(*sceneWithComponents.audioChannels.toTypedArray(), sceneId = sceneId)
            insertFxSendWithSceneId(*sceneWithComponents.fxSends.toTypedArray(), sceneId = sceneId)
        }
    }

    suspend fun insertMasterChannelWithSceneId(vararg masterChannels: MasterChannel, sceneId: Long) {
        for(masterChannel in masterChannels) {
            masterChannel.sceneId = sceneId
            masterChannel.masterId = insertMasterChannel(masterChannel)
        }
    }

    suspend fun insertAudioChannelWithSceneId(vararg audioChannels: AudioChannel, sceneId: Long) {
        for(audioChannel in audioChannels) {
            audioChannel.sceneId = sceneId
            audioChannel.audioChannelId = insertAudioChannel(audioChannel)
        }
    }

    suspend fun insertFxSendWithSceneId(vararg fxSends: FxSend, sceneId: Long) {
        for(fxSend in fxSends) {
            fxSend.sceneId = sceneId
            fxSend.fxSendId = insertFxSend(fxSend)
        }
    }

    suspend fun addPreset(preset: Preset): PresetWithScenes {
        val presetWithScenes = PresetWithScenes.newInstance(preset)
        insertPresetWithScenes(presetWithScenes)
        return presetWithScenes
    }
//endregion insert

//region update
    @Update
    suspend fun updatePreset(vararg preset: Preset)

    @Update
    suspend fun updateScene(vararg scene: Scene)

    @Update
    suspend fun updateMasterChannel(vararg masterChannel: MasterChannel)

    @Update
    suspend fun updateAudioChannel(vararg audioChannel: AudioChannel)

    @Update
    suspend fun updateFxSend(vararg fxSend: FxSend)

    @Update
    suspend fun updateCurrentPreset(vararg currentPreset: CurrentPreset)

    @Transaction
    suspend fun updatePresetWithScenes(presetWithScenes: PresetWithScenes, updateAll: Boolean) {
        updatePreset(presetWithScenes.preset)
        updateSceneWithComponents(*presetWithScenes.scenes.toTypedArray(), updateAll = updateAll)
    }

    @Transaction
    suspend fun updateSceneWithComponentsInTransaction(vararg sceneWithComponents: SceneWithComponents, updateAll: Boolean) {
        updateSceneWithComponents(*sceneWithComponents, updateAll = updateAll)
    }

    suspend fun updateSceneWithComponents(vararg scenesWithComponents: SceneWithComponents, updateAll: Boolean) {
        for(sceneWithComponents in scenesWithComponents) {
            updateScene(sceneWithComponents.scene)
            updateMasterChannels(sceneWithComponents, updateAll)
            updateAudioChannels(sceneWithComponents, updateAll)
            updateFxSends(sceneWithComponents, updateAll)
        }
    }

    suspend fun updateMasterChannels(
        sceneWithComponents: SceneWithComponents,
        updateAll: Boolean
    ) {
        for (masterChannel in sceneWithComponents.masterChannels)
            if (masterChannel.isDirty || updateAll) {
                masterChannel.isDirty = false
                updateMasterChannel(masterChannel)
            }
    }

    suspend fun updateAudioChannels(
        sceneWithComponents: SceneWithComponents,
        updateAll: Boolean
    ) {
        for (audioChannel in sceneWithComponents.audioChannels) {
            if (audioChannel.isDirty || updateAll) {
                audioChannel.isDirty = false
                updateAudioChannel(audioChannel)
            }
        }

    }

    suspend fun updateFxSends(
        sceneWithComponents: SceneWithComponents,
        updateAll: Boolean
    ) {
        for (fxSend in sceneWithComponents.fxSends)
            if (fxSend.isDirty || updateAll) {
                fxSend.isDirty = true
                updateFxSend(fxSend)
            }
    }
//endregion update

//region delete
    @Delete
    fun deletePreset(preset: Preset)

    @Delete
    fun deleteScene(scene: Scene)
//endregion delete

}