package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Scene(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "scene_name")var channelId : String,

    @Embedded
    @ColumnInfo(name = "fx_settings")var fxSettings: FxSettings
)