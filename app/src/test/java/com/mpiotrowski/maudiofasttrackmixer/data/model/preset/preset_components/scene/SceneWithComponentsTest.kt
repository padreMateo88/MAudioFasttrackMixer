package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

import org.junit.Test

class SceneWithComponentsTest {

    @Test
    fun newInstance_instanceWithCorrectParametersCreated() {

        val testSceneName = "testScene"
        val testPresetId = "wlekrjw"
        val testSceneOrder = 3

        val testSceneWithComponents = SceneWithComponents.newInstance(testSceneName, testPresetId, testSceneOrder)

        MatcherAssert.assertThat(testSceneWithComponents.scene.sceneName, Matchers.`is`(testSceneName))
        MatcherAssert.assertThat(testSceneWithComponents.scene.presetId, Matchers.`is`(testPresetId))
        MatcherAssert.assertThat(testSceneWithComponents.scene.sceneOrder, Matchers.`is`(testSceneOrder))
    }

    @Test
    fun copyValues_valuesCopiedCorrectly() {

        val copyFromSceneName = "copyFromSceneName"
        val copyFromPresetId = "copyFromPresetId"
        val copyFromSceneOrder = 3

        val copyToSceneName = "copyToSceneName"
        val copyToPresetId = "copyToPresetId"
        val copyToSceneOrder = 4


        val copyFromScene = SceneWithComponents.newInstance(copyFromSceneName, copyFromPresetId, copyFromSceneOrder)
        val copyToScene = SceneWithComponents.newInstance(copyToSceneName, copyToPresetId, copyToSceneOrder)

        SceneTestUtil.setSampleValuesToScene(copyFromScene)

        //method under test
        copyToScene.copyValues(copyFromScene, copyFromScene.scene.sceneName)

        SceneTestUtil.assertSameValuesInScenes(copyToScene, copyFromScene)
    }
}