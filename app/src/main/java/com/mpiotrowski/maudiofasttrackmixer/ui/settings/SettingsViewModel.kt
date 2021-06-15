package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbController
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import javax.inject.Inject

class SettingsViewModel @Inject constructor(repository: Repository, private var usbController: UsbController) : ViewModel() {

    private val currentState = repository.currentModelState
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
        usbController.setFxVolume(fxVolume*1.27.toInt())
    }

    fun onFxDurationChanged(fxDuration: Int) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "fxDuration $fxDuration")
        usbController.setFxDuration(fxDuration*1.27.toInt())
    }

    fun onFxFeedbackChanged(fxFeedback: Int) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "fxFeedback $fxFeedback")
        usbController.setFxFeedback((fxFeedback*1.27.toInt()))
    }

    fun onFxTypeChanged(fxType: FxSettings.FxType) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "fxType $fxType")
        usbController.setFxType(fxType)
    }

    fun onSampleRateChanged(sampleRate: SampleRate) {
        currentState.value?.preset?.isDirty = true
        LogUtil.d( "sampleRate $sampleRate")
        usbController.setSampleRate(sampleRate)
    }
//endregion FX listeners
}