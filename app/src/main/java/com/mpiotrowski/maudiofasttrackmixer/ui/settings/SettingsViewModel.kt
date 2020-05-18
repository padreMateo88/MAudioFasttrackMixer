package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.MAudioFasttrackMixerApplication
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MAudioFasttrackMixerApplication).repository

    private val currentState = repository.currentState
    private val currentScene = repository.currentScene
    val fxSettings = MediatorLiveData<FxSettings>()
    val sampleRate = MediatorLiveData<SampleRate>()

    init {
        fxSettings.addSource(currentScene) {
            sceneWithComponents ->
            fxSettings.value = sceneWithComponents.scene.fxSettings
        }

        sampleRate.addSource(currentState) {
                presetWithComponents -> sampleRate.value = presetWithComponents.preset.sampleRate
        }
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