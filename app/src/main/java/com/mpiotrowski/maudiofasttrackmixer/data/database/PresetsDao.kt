package com.mpiotrowski.maudiofasttrackmixer.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes

@Dao
interface PresetsDao {
    @Transaction
    @Query("SELECT * FROM Preset")
    fun getPresetsWithScenes(): List<PresetWithScenes>
}