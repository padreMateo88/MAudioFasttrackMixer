package com.mpiotrowski.maudiofasttrackmixer.data.database

import android.transition.Scene
import androidx.room.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

@Dao
interface PresetsDao {

//region select
    @Transaction
    @Query("SELECT * FROM Preset")
    fun getPresetsWithScenes(): List<PresetWithScenes>
//endregion select

//region insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPreset(preset: Preset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScene(scene: Scene): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMasterChannel(masterChannel: MasterChannel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAudioChannel(audioChannel: AudioChannel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFxSend(fxSend: FxSend): Long
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