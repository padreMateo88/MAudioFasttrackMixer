package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbController
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MixerViewModel @Inject constructor(private val repository: Repository, private var usbController: UsbController) : ViewModel() {

    private val currentState = repository.currentModelState
    private val currentOutput = MutableLiveData<Int>()

    val currentScene = repository.currentScene
    val audioChannels = MediatorLiveData<List<AudioChannel>>()
    val masterChannel = MediatorLiveData<MasterChannel>()
    val fxSends = MediatorLiveData<List<FxSend>>()
    val fine = MutableLiveData<Boolean>()
    val deviceOnline = MutableLiveData<Boolean>()

    init {
        audioChannels.addSource(currentOutput) { outputIndex ->
            audioChannels.value = currentScene.value?.channelsByOutputsMap?.get(outputIndex)
        }

        audioChannels.addSource(currentScene) { sceneWithComponents ->
            audioChannels.value = sceneWithComponents?.channelsByOutputsMap?.get(currentOutput.value)
        }

        masterChannel.addSource(currentOutput) { outputIndex ->
            masterChannel.value = currentScene.value?.mastersByOutputsMap?.get(outputIndex)
        }

        masterChannel.addSource(currentScene) { sceneWithComponents ->
            masterChannel.value = sceneWithComponents?.mastersByOutputsMap?.get(currentOutput.value)
        }

        fxSends.addSource(currentScene) { sceneWithComponents ->
            fxSends.value = sceneWithComponents.fxSends
        }

        currentOutput.value = 1
    }

    fun getAudioChannelsNumber(): Int {
        return audioChannels.value?.size ?: 0
    }

    fun getCurrentSceneOrder(): Int? {
        return currentScene.value?.scene?.sceneOrder
    }

    fun getSceneOfCurrentState(order: Int): SceneWithComponents? {
        return currentState.value?.scenesByOrder?.get(order)
    }

    fun getCurrentState(): PresetWithScenes? {
        return currentState.value
    }

//region scene control
    fun saveSceneAs(copyFrom: SceneWithComponents, copyTo: SceneWithComponents, newName: String) {
        if(copyFrom.scene.sceneId != copyTo.scene.sceneId) {
            copyTo.copyValues(copyFrom, newName)
        } else
            repository.setCurrentSceneName(newName)

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSceneOfCurrentPresetAs(copyTo)
        }
    }

    fun onSceneSelected(sceneIndex: Int) {
        repository.setSelectedScene(sceneIndex)
    }
//endregion scene control

//region mixer parameters listener
    fun onOutputSelected(outputIndex: Int) {
        LogUtil.d("output $outputIndex")
        currentOutput.value = outputIndex
    }

    fun onChannelChanged(audioChannel: AudioChannel) {
        currentState.value?.preset?.isDirty = true
        audioChannel.isDirty = true
        LogUtil.d("channel ${audioChannel.inputIndex} volume ${audioChannel.volume} panorama ${audioChannel.panorama} mute ${audioChannel.mute} solo ${audioChannel.solo}")
    }

    fun onSoloChanged(audioChannel: AudioChannel) {
        if (!audioChannel.solo)
            return

        for(audioChannelItem in this.audioChannels.value!!) {
            if(audioChannelItem.solo && audioChannelItem != audioChannel) {
                audioChannels.mutation {
                    audioChannelItem.solo = false
                }
            }
        }
    }

    fun onFxSendChanged(fxSend: FxSend) {
        currentState.value?.preset?.isDirty = true
        fxSend.isDirty = true
        LogUtil.d("channel $fxSend")
    }

    fun onFxReturnChanged(masterChannel: MasterChannel, fxReturn: Int) {
        currentState.value?.preset?.isDirty = true
        masterChannel.isDirty
        LogUtil.d("fxReturn $fxReturn")
    }

    fun onMasterVolumeChanged(masterChannel: MasterChannel) {
        currentState.value?.preset?.isDirty = true
        masterChannel.isDirty = true
        LogUtil.d("master volume ${masterChannel.volume}")
    }
//endregion mixer parameters listener
}