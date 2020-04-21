package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.room.PrimaryKey

data class Mixer(@PrimaryKey val id: Int, var output : Int)