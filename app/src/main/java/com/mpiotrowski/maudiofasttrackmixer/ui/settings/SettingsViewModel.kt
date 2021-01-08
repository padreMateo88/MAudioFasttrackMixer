package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import android.util.Log
import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import javax.inject.Inject

class SettingsViewModel @Inject constructor(repository: Repository) : ViewModel() {

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
        LogUtil.d( "fxVolume $fxVolume")
    }

    fun onFxDurationChanged(fxDuration: Int) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "fxDuration $fxDuration")
    }

    fun onFxFeedbackChanged(fxFeedback: Int) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "fxFeedback $fxFeedback")
    }

    fun onFxTypeChanged(fxType: FxSettings.FxType) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "fxType $fxType")
    }

    fun onSampleRateChanged(sampleRate: SampleRate) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "sampleRate $sampleRate")
    }
//endregion FX listeners
}