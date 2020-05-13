package com.mpiotrowski.maudiofasttrackmixer.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_INPUTS_COUNT
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_STEREO_OUTPUTS_COUNT
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

@Dao
interface PresetsDao {

//region select
    @Query("SELECT * FROM Preset WHERE presetId != \"$LAST_PERSISTED_STATE_ID\"")
    fun getPresetsWithScenes(): LiveData<List<PresetWithScenes>>

    @Query("SELECT * FROM Preset WHERE presetId = :presetId")
    fun getPreset(presetId: String): List<PresetWithScenes>

    @Query("SELECT * FROM CurrentPreset")
    suspend fun getCurrentPreset(): List<CurrentPreset>

    @Query("SELECT * FROM CurrentPreset")
    fun getCurrentPresetLiveData(): LiveData<List<CurrentPreset>>

    @Query("SELECT * FROM Preset WHERE presetId = \"$LAST_PERSISTED_STATE_ID\"")
    fun getPersistedState(): LiveData<PresetWithScenes>

//endregion select

//region insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentPreset(currentPreset: CurrentPreset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: Preset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: Scene): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterChannel(vararg masterChannel: MasterChannel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioChannel(vararg audioChannel: AudioChannel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFxSend(vararg fxSend: FxSend)

    @Transaction
    suspend fun insertPresetWithScenes(presetWithScenes: PresetWithScenes) {
        insertPreset(presetWithScenes.preset)
        for(sceneWithComponents in presetWithScenes.scenes) {
            val sceneId = insertScene(sceneWithComponents.scene)
            insertMasterChannelWithSceneId(*sceneWithComponents.masterChannels.toTypedArray(), sceneId = sceneId)
            insertAudioChannelWithSceneId(*sceneWithComponents.audioChannels.toTypedArray(), sceneId = sceneId)
            insertFxSendWithSceneId(*sceneWithComponents.fxSends.toTypedArray(), sceneId = sceneId)
        }
    }

    suspend fun insertMasterChannelWithSceneId(vararg masterChannels: MasterChannel, sceneId: Long) {
        for(masterChannel in masterChannels)
            masterChannel.sceneId = sceneId
        insertMasterChannel(*masterChannels)
    }

    suspend fun insertAudioChannelWithSceneId(vararg audioChannels: AudioChannel, sceneId: Long) {
        for(audioChannel in audioChannels)
            audioChannel.sceneId = sceneId
        insertAudioChannel(*audioChannels)
    }

    suspend fun insertFxSendWithSceneId(vararg fxSends: FxSend, sceneId: Long) {
        for(fxSend in fxSends)
            fxSend.sceneId = sceneId
        insertFxSend(*fxSends)
    }

    suspend fun addPreset(preset: Preset): PresetWithScenes {
        val presetWithScenes = PresetWithScenes.newInstance(preset)
        insertPresetWithScenes(presetWithScenes)
        return presetWithScenes
    }
//endregion insert

//region update
    @Update
    fun updatePreset(vararg preset: Preset)

    @Update
    fun updateScene(vararg scene: Scene)

    @Update
    fun updateMasterChannel(vararg masterChannel: MasterChannel)

    @Update
    fun updateAudioChannel(vararg audioChannel: AudioChannel)

    @Update
    fun updateFxSend(vararg fxSend: FxSend)

    @Update
    fun updateCurrentPreset(vararg currentPreset: CurrentPreset)

    @Transaction
    fun updatePresetWithScenes(presetWithScenes: PresetWithScenes, updateAll: Boolean) {
        updatePreset(presetWithScenes.preset)
        updateSceneWithComponents(*presetWithScenes.scenes.toTypedArray(), updateAll = updateAll)
    }

    @Transaction
    suspend fun updateSceneWithComponentsInTransaction(vararg sceneWithComponents: SceneWithComponents, updateAll: Boolean) {
        updateSceneWithComponents(*sceneWithComponents, updateAll = updateAll)
    }

    fun updateSceneWithComponents(vararg scenesWithComponents: SceneWithComponents, updateAll: Boolean) {
        for(sceneWithComponents in scenesWithComponents) {
            updateScene(sceneWithComponents.scene)
            updateMasterChannels(sceneWithComponents, updateAll)
            updateAudioChannels(sceneWithComponents, updateAll)
            updateFxSends(sceneWithComponents, updateAll)
        }
    }

    fun updateMasterChannels(
        sceneWithComponents: SceneWithComponents,
        updateAll: Boolean
    ) {
        for (masterChannel in sceneWithComponents.masterChannels)
            if (masterChannel.isDirty || updateAll)
                updateMasterChannel(masterChannel)
    }

    fun updateAudioChannels(
        sceneWithComponents: SceneWithComponents,
        updateAll: Boolean
    ) {
        for (audioChannel in sceneWithComponents.audioChannels)
            if (audioChannel.isDirty || updateAll)
                updateAudioChannel(audioChannel)
    }

    fun updateFxSends(
        sceneWithComponents: SceneWithComponents,
        updateAll: Boolean
    ) {
        for (fxSend in sceneWithComponents.fxSends)
            if (fxSend.isDirty || updateAll)
                updateFxSend(fxSend)
    }
//endregion update

//region delete
    @Delete
    fun deletePreset(preset: Preset)

    @Delete
    fun deleteScene(scene: Scene)
//endregion delete

}