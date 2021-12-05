package com.mpiotrowski.maudiofasttrackmixer.data.database

import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.junit.BeforeClass
import org.junit.Test

class ConvertersTest {

    companion object {

        lateinit var converters: Converters

        @BeforeClass
        @JvmStatic fun setup() {
            converters = Converters()
        }
    }

    //toFxType
    @Test
    fun toFxType_correctApiCodes_returnCorrespondingEnums() {
        MatcherAssert.assertThat(Converters().toFxType(0x0500), `is` (FxSettings.FxType.PLATE))
        MatcherAssert.assertThat(Converters().toFxType(0x0000), `is` (FxSettings.FxType.ROOM1))
    }

    @Test
    fun toFxType_otherValues_returnROOM1() {
        MatcherAssert.assertThat(Converters().toFxType(0x0501), `is` (FxSettings.FxType.ROOM1))
        MatcherAssert.assertThat(Converters().toFxType(0x0001), `is` (FxSettings.FxType.ROOM1))
    }

    //fromFxType
    @Test
    fun toFxType_givenFxTypeEnum_returnsCorrectApiCode() {
        MatcherAssert.assertThat(Converters().fromFxType(FxSettings.FxType.ROOM1), `is` (0x0000))
        MatcherAssert.assertThat(Converters().fromFxType(FxSettings.FxType.PLATE), `is` (0x0500))
    }

    //toSampleRate
    @Test
    fun toSampleRate_givenAvailableSampleRate_returnsCorrectEnum() {
        MatcherAssert.assertThat(Converters().toSampleRate(44100), `is` (SampleRate.SR_44_1))
        MatcherAssert.assertThat(Converters().toSampleRate(48000), `is` (SampleRate.SR_48))
        MatcherAssert.assertThat(Converters().toSampleRate(88200), `is` (SampleRate.SR_88_2))
        MatcherAssert.assertThat(Converters().toSampleRate(96000), `is` (SampleRate.SR_96))
    }

    //toSampleRate
    @Test
    fun toSampleRate_givenSampleRateOtherThanAvailable_returnsNull() {
        MatcherAssert.assertThat(Converters().toSampleRate(44200), `is` (nullValue()))
        MatcherAssert.assertThat(Converters().toSampleRate(0), `is` (nullValue()))
    }

    //fromSampleRate
    @Test
    fun fromSampleRate_givenSampleRateEnum_returnsCorrectSampleRate() {
        MatcherAssert.assertThat(Converters().fromSampleRate(SampleRate.SR_44_1), `is` (44100))
        MatcherAssert.assertThat(Converters().fromSampleRate(SampleRate.SR_48), `is` (48000))
        MatcherAssert.assertThat(Converters().fromSampleRate(SampleRate.SR_88_2), `is` (88200))
        MatcherAssert.assertThat(Converters().fromSampleRate(SampleRate.SR_96), `is` (96000))
    }
}