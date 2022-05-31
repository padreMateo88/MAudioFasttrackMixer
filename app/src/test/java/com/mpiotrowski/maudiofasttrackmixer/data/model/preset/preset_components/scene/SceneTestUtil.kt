package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

class SceneTestUtil {
    companion object{

        //region setSampleValues
        fun setSampleValuesToScene(sceneWithComponents: SceneWithComponents) {
            setSampleValuesToFxSettings(sceneWithComponents)

            sceneWithComponents.scene.sceneName = "Test scene ${sceneWithComponents.scene.sceneOrder}"

            setSampleValuesToAudioChannels(sceneWithComponents)
            setSampleValuesToFxSends(sceneWithComponents)
            setSampleValuesToMasterChannels(sceneWithComponents)
        }

        private fun setSampleValuesToMasterChannels(sceneWithComponents: SceneWithComponents) {
            for (masterKey in sceneWithComponents.mastersByOutputsMap.keys) {
                val master = sceneWithComponents.mastersByOutputsMap[masterKey]
                master?.volume = masterKey
                master?.volume = masterKey
                master?.panorama = masterKey
                master?.fxReturn = masterKey
                master?.mute = true
            }
        }

        private fun setSampleValuesToFxSends(sceneWithComponents: SceneWithComponents) {
            for (fxSendKey in sceneWithComponents.fxSendsMap.keys) {
                val fxSend = sceneWithComponents.fxSendsMap[fxSendKey]
                fxSend?.volume = fxSendKey
            }
        }

        private fun setSampleValuesToAudioChannels(sceneWithComponents: SceneWithComponents) {
            for (audioChannel in sceneWithComponents.audioChannels) {
                audioChannel.volume = audioChannel.inputIndex
                audioChannel.mute = true
                audioChannel.panorama = audioChannel.inputIndex
                audioChannel.solo = true
            }
        }

        private fun setSampleValuesToFxSettings(sceneWithComponents: SceneWithComponents) {
            sceneWithComponents.scene.fxSettings.duration = sceneWithComponents.scene.sceneOrder
            sceneWithComponents.scene.fxSettings.feedback = sceneWithComponents.scene.sceneOrder
            sceneWithComponents.scene.fxSettings.fxMute = true
            sceneWithComponents.scene.fxSettings.fxType = FxSettings.FxType.HALL2
            sceneWithComponents.scene.fxSettings.volume = sceneWithComponents.scene.sceneOrder
        }

        //endregion setSampleValues

        //region assertions

        fun assertSameValuesInScenes(
            destinationPresetSceneWithComponents: SceneWithComponents?,
            copyFromSceneWithComponents: SceneWithComponents?
        ) {
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.sceneName,
                Matchers.`is`(copyFromSceneWithComponents?.scene?.sceneName)
            )
            assertSameFxSettingsValues(
                copyFromSceneWithComponents,
                destinationPresetSceneWithComponents
            )
            assertSameAudioChannelsValues(
                copyFromSceneWithComponents,
                destinationPresetSceneWithComponents
            )
            assertSameFxSendsValues(
                copyFromSceneWithComponents,
                destinationPresetSceneWithComponents
            )
            assertSameMasterChannelValues(
                copyFromSceneWithComponents,
                destinationPresetSceneWithComponents
            )
        }

        fun assertSameFxSettingsValues(
            copyFromSceneWithComponents: SceneWithComponents?,
            destinationPresetSceneWithComponents: SceneWithComponents?
        ) {
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.fxSettings?.duration,
                Matchers.`is`(copyFromSceneWithComponents?.scene?.fxSettings?.duration)
            )
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.fxSettings?.feedback,
                Matchers.`is`(copyFromSceneWithComponents?.scene?.fxSettings?.feedback)
            )
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.fxSettings?.fxMute,
                Matchers.`is`(copyFromSceneWithComponents?.scene?.fxSettings?.fxMute)
            )
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.fxSettings?.fxType,
                Matchers.`is`(copyFromSceneWithComponents?.scene?.fxSettings?.fxType)
            )
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.fxSettings?.isDirty,
                Matchers.`is`(true)
            )
            MatcherAssert.assertThat(
                destinationPresetSceneWithComponents?.scene?.fxSettings?.volume,
                Matchers.`is`(copyFromSceneWithComponents?.scene?.fxSettings?.volume)
            )
        }

        fun assertSameMasterChannelValues(
            copyFromSceneWithComponents: SceneWithComponents?,
            destinationPresetSceneWithComponents: SceneWithComponents?
        ) {
            for (masterKey in copyFromSceneWithComponents!!.mastersByOutputsMap.keys) {
                val copyFromMaster = copyFromSceneWithComponents.mastersByOutputsMap[masterKey]
                val destinationMaster = destinationPresetSceneWithComponents?.mastersByOutputsMap?.get(masterKey)
                MatcherAssert.assertThat(destinationMaster?.volume,
                    Matchers.`is`(copyFromMaster?.volume)
                )
                MatcherAssert.assertThat(destinationMaster?.isDirty, Matchers.`is`(true))
                MatcherAssert.assertThat(destinationMaster?.volume,
                    Matchers.`is`(copyFromMaster?.volume)
                )
                MatcherAssert.assertThat(destinationMaster?.panorama,
                    Matchers.`is`(copyFromMaster?.panorama)
                )
                MatcherAssert.assertThat(destinationMaster?.fxReturn,
                    Matchers.`is`(copyFromMaster?.fxReturn)
                )
                MatcherAssert.assertThat(destinationMaster?.mute, Matchers.`is`(copyFromMaster?.mute))
            }
        }

        fun assertSameFxSendsValues(copyFromSceneWithComponents: SceneWithComponents?, destinationSceneWithComponents: SceneWithComponents?) {
            for (fxSendKey in copyFromSceneWithComponents!!.fxSendsMap.keys) {
                val copyFromFxSend = copyFromSceneWithComponents.fxSendsMap[fxSendKey]
                val destinationFxSend = destinationSceneWithComponents?.fxSendsMap?.get(fxSendKey)
                MatcherAssert.assertThat(destinationFxSend?.isDirty, Matchers.`is`(true))
                MatcherAssert.assertThat(destinationFxSend?.volume,
                    Matchers.`is`(copyFromFxSend?.volume)
                )
            }
        }

        fun assertSameAudioChannelsValues(
            copyFromSceneWithComponents: SceneWithComponents?,
            destinationPresetSceneWithComponents: SceneWithComponents?
        ) {
            for (audioChannelIndex in copyFromSceneWithComponents?.audioChannels?.indices!!) {

                val copyFromAudioChannel = copyFromSceneWithComponents.audioChannels[audioChannelIndex]
                val destinationAudioChannel = destinationPresetSceneWithComponents?.audioChannels?.get(audioChannelIndex)

                MatcherAssert.assertThat(destinationAudioChannel?.volume,
                    Matchers.`is`(copyFromAudioChannel.volume)
                )
                MatcherAssert.assertThat(destinationAudioChannel?.isDirty, Matchers.`is`(true))
                MatcherAssert.assertThat(destinationAudioChannel?.mute,
                    Matchers.`is`(copyFromAudioChannel.mute)
                )
                MatcherAssert.assertThat(destinationAudioChannel?.panorama,
                    Matchers.`is`(copyFromAudioChannel.panorama)
                )
                MatcherAssert.assertThat(destinationAudioChannel?.solo,
                    Matchers.`is`(copyFromAudioChannel.solo)
                )
            }
        }
        //endregion assertions
    }
}