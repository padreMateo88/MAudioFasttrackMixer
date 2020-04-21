package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FxSend(@PrimaryKey var id: Int,
                  @ColumnInfo(name = "channel_number")var channelId : Int = 0,
                  var volume : Int = 0
)