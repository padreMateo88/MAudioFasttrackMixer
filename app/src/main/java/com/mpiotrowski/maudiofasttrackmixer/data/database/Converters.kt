package com.mpiotrowski.maudiofasttrackmixer.data.database

import androidx.room.TypeConverter
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings

class Converters {
    @TypeConverter
    fun toFxType(value: Int): FxSettings.FxType {
        for(fxType in FxSettings.FxType.values()) {
            if(fxType.apiCode == value) {
                return fxType
            }
        }
        return FxSettings.FxType.ROOM1
    }

    @TypeConverter
    fun fromFxType(fxType: FxSettings.FxType): Int {
        return fxType.apiCode
    }

    @TypeConverter
    fun toSampleRate(value: Int?): SampleRate? {
        for(sampleRate in SampleRate.values()) {
            if(sampleRate.frequencyValue == value) {
                return sampleRate
            }
        }
        return null
    }

    @TypeConverter
    fun fromSampleRate(sampleRate: SampleRate): Int {
        return sampleRate.frequencyValue
  }
}