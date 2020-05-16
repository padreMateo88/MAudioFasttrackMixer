package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*
import com.mpiotrowski.maudiofasttrackmixer.util.Event
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())

    val currentState = MediatorLiveData<PresetWithScenes>()
    val currentScene = MediatorLiveData<SceneWithComponents>()
    val fxSettings = MediatorLiveData<FxSettings>()
    val sampleRate = MediatorLiveData<SampleRate>()

    init {
        currentState.addSource(repository.currentState) {
            if(currentState.value == null)
                currentState.value = repository.currentState.value
        }

        fxSettings.addSource(currentScene) {
            sceneWithComponents ->
            Log.d("MPdebug","fxSettings")
            fxSettings.value = sceneWithComponents.scene.fxSettings
        }

        sampleRate.addSource(currentState) {
                presetWithComponents -> sampleRate.value = presetWithComponents.preset.sampleRate
        }

        currentScene.addSource(currentState) { value ->
            val scenesById = value.scenes.map {it.scene.sceneId to it}.toMap()

            if(scenesById.containsKey(currentScene.value?.scene?.sceneId))
                currentScene.value = scenesById[currentScene.value?.scene?.sceneId]
            else
                currentScene.value = value?.scenesByOrder?.get(1)
        }

//      TODO
//        currentScene.addSource(sceneLoadedEvent) { sceneSelectedEvent ->
//            sceneSelectedEvent.getContentIfNotHandled()?.let {
//                sceneIndex ->
//                currentScene.value = currentState.value?.scenesByOrder?.get(sceneIndex)
//            }
//        }
    }

//region FX listeners
    fun onFxVolumeChanged(fxVolume: Int) {
        currentState.value?.preset?.isDirty = true
        Log.d("MPdebug", "fxVolume $fxVolume")
    }

    fun onFxDurationChanged(fxDuration: Int) {
        currentState.value?.preset?.isDirty = true
        Log.d("MPdebug", "fxDuration $fxDuration")
    }

    fun onFxFeedbackChanged(fxFeedback: Int) {
        currentState.value?.preset?.isDirty = true
        Log.d("MPdebug", "fxFeedback $fxFeedback")
    }

    fun onFxTypeChanged(fxType: FxSettings.FxType) {
        currentState.value?.preset?.isDirty = true
        Log.d("MPdebug", "fxType $fxType")
    }

    fun onSampleRateChanged(sampleRate: SampleRate) {
        currentState.value?.preset?.isDirty = true
        Log.d("MPdebug", "sampleRate $sampleRate")
    }
//endregion FX listeners
}