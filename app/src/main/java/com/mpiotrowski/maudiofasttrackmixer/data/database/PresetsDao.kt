package com.mpiotrowski.maudiofasttrackmixer.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

@Dao
interface PresetsDao {

//region select
    @Transaction
    @Query("SELECT * FROM Preset")
    fun getPresetsWithScenes(): LiveData<List<PresetWithScenes>>
//endregion select

//region insert
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
//endregion insert

//region update
    @Update
    fun updatePreset(vararg preset: Preset)

    @Update
    fun updateScene(vararg scene: Scene)

    @Update
    fun updateMasterChannel( masterChannel: MasterChannel)

    @Update
    fun updateAudioChannel(vararg audioChannel: AudioChannel)

    @Update
    fun updateFxSend(vararg  fxSend: FxSend)
//endregion update

//region delete
    @Delete
    fun deletePreset(preset: Preset)

    @Delete
    fun deleteScene(scene: Scene)
//endregion delete

}