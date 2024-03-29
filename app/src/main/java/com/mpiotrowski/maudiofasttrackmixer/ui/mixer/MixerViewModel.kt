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
    val muteFx = MediatorLiveData<Boolean>()

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

        muteFx.addSource(currentScene) { sceneWithComponents ->
            muteFx.value = sceneWithComponents?.scene?.fxSettings?.fxMute
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
        currentScene.value?.let {
            usbController.loadMixerState(it)
        }
    }

    fun loadMixerState(scene:  SceneWithComponents) {
        usbController.loadMixerState(scene)
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
        masterChannel.value?.let { masterChannel ->
            currentOutput.value?.let { currentOutput ->
                setChannelVolume(audioChannel, currentOutput, masterChannel)
            }
        }
    }

    private fun isAnySolo(): Boolean {
        this.audioChannels.value?.let {
            for(audioChannelItem in it) {
                if(audioChannelItem.solo)
                    return true
            }
        }
        return false
    }

    private fun setChannelVolume(
        audioChannel: AudioChannel,
        currentOutput: Int,
        masterChannel: MasterChannel
    ) {
        val mute = when {
            masterChannel.mute -> true
            audioChannel.solo -> false
            isAnySolo() -> true
            else -> audioChannel.mute
        }
        LogUtil.d("setChannelVolume ${audioChannel.inputIndex} mute $mute")
        usbController.setChannelVolume(
            volume = audioChannel.volume * 100,
            pan = audioChannel.panorama,
            input = audioChannel.inputIndex,
            outputPair = currentOutput,
            masterVolume = masterChannel.volume * 100,
            masterPan = masterChannel.panorama,
            mute = mute
        )
    }

    fun onSoloChanged(audioChannel: AudioChannel, isChecked: Boolean) {
        LogUtil.d("onSoloChange ${audioChannel.inputIndex} isChecked $isChecked")
        if(audioChannel.solo != isChecked)
            audioChannel.solo = isChecked

        this.audioChannels.value?.let {
            for(audioChannelItem in it) {
                if (audioChannelItem.solo && audioChannelItem.inputIndex != audioChannel.inputIndex)
                    audioChannels.mutation {
                        audioChannelItem.solo = false
                    }

                onChannelChanged(audioChannelItem)
            }
        }
    }

    fun onFxMuteChanged(muteFx: Boolean) {
        currentScene.value?.scene?.fxSettings?.fxMute = muteFx
        currentState.value?.preset?.isDirty = true
        currentScene.value?.scene?.fxSettings?.let { usbController.setFxVolume( if(muteFx) 1 else it.volume) }
    }

    fun onFxSendChanged(fxSend: FxSend, audioChannel: AudioChannel) {
        currentState.value?.preset?.isDirty = true
        fxSend.isDirty = true
        LogUtil.d("channel $fxSend")
        usbController.setFxSend(fxSend.volume * 100, fxSend.inputIndex, audioChannel.volume * 100, audioChannel.mute && !audioChannel.solo)
    }

    fun onFxReturnChanged(masterChannel: MasterChannel) {
        currentState.value?.preset?.isDirty = true
        masterChannel.isDirty = true
        LogUtil.d("fxReturn ${masterChannel.fxReturn} output ${masterChannel.outputIndex}")
        usbController.setFxReturn(masterChannel.fxReturn * 100, masterChannel)
    }

    fun onMasterVolumeChanged(masterChannel: MasterChannel) {
        currentState.value?.preset?.isDirty = true
        masterChannel.isDirty = true
        LogUtil.d("master volume ${masterChannel.volume}")
        this.audioChannels.value?.let {
            for(audioChannelItem in it) {
                onChannelChanged(audioChannelItem)
            }
        }
        onFxReturnChanged(masterChannel)
    }
//endregion mixer parameters listener
}