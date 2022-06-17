package com.mpiotrowski.maudiofasttrackmixer.data.model.preset

import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneTestUtil
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneTestUtil.Companion.assertSameValuesInScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.Test

class PresetWithScenesTest {

    @Test
    fun copyValues_valuesInBothPresetsMatch() {

        val copyFromPresetName = "copyFromPreset"
        val copyFromPreset = PresetWithScenes.newInstance(Preset(presetName = copyFromPresetName))
        val destinationPresetName = "destinationPresetName"
        val destinationPreset = PresetWithScenes.newInstance(Preset(presetName = destinationPresetName))
        val newPresetName = "newPresetName"

        setExampleValuesToCopiedPreset(copyFromPreset)

        //method under test
        destinationPreset.copyValues(copyFromPreset,newPresetName)

        assertSameValuesInCopyFormAndDestinationPreset(copyFromPreset, destinationPreset)

    }

    @Test
    fun newInstance_correctNumberOfScenesInCreatedPreset() {

        val testPreset = PresetWithScenes.newInstance(Preset(presetName = "TestPreset"))
        MatcherAssert.assertThat(testPreset.scenes.size, `is`(SCENES_IN_PRESET_COUNT))
    }

    private fun setExampleValuesToCopiedPreset(copyFromPreset: PresetWithScenes) {
        copyFromPreset.preset.sampleRate = SampleRate.SR_96

        for (sceneWithComponents: SceneWithComponents in copyFromPreset.scenes) {
            SceneTestUtil.setSampleValuesToScene(sceneWithComponents)
        }
    }

    private fun assertSameValuesInCopyFormAndDestinationPreset(copyFromPreset: PresetWithScenes, destinationPreset: PresetWithScenes) {

        MatcherAssert.assertThat(destinationPreset.preset.isDirty, `is`(true))
        MatcherAssert.assertThat(copyFromPreset.preset.sampleRate,`is`(destinationPreset.preset.sampleRate))

        for (sceneOrder in copyFromPreset.scenesByOrder.keys) {

            val copyFromSceneWithComponents = copyFromPreset.scenesByOrder[sceneOrder]
            val destinationPresetSceneWithComponents = destinationPreset.scenesByOrder[sceneOrder]

            assertSameValuesInScenes(destinationPresetSceneWithComponents, copyFromSceneWithComponents)
        }
    }
}