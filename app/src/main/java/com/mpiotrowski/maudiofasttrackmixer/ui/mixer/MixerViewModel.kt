package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

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
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel
import com.mpiotrowski.maudiofasttrackmixer.util.Event
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MixerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())

    val currentState = MediatorLiveData<PresetWithScenes>()
    private val sceneLoadedEvent = MutableLiveData<Event<Int>>()
    private val currentOutput = MutableLiveData<Int>()
    val currentScene = MediatorLiveData<SceneWithComponents>()
    val audioChannels = MediatorLiveData<List<AudioChannel>>()
    val masterChannel = MediatorLiveData<MasterChannel>()
    val fxSends = MediatorLiveData<List<FxSend>>()
    val fine = MutableLiveData<Boolean>()
    val deviceOnline = MutableLiveData<Boolean>()

    init {

        currentState.addSource(repository.currentState) {
            if(currentState.value == null)
                currentState.value = repository.currentState.value
        }

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

        currentScene.addSource(currentState) { value ->
            val scenesById = value.scenes.map {it.scene.sceneId to it}.toMap()

            if(scenesById.containsKey(currentScene.value?.scene?.sceneId))
                currentScene.value = scenesById[currentScene.value?.scene?.sceneId]
            else
                currentScene.value = value?.scenesByOrder?.get(1)
        }

        currentScene.addSource(sceneLoadedEvent) { sceneSelectedEvent ->
            sceneSelectedEvent.getContentIfNotHandled()?.let {
                    sceneIndex ->
                currentScene.value = currentState.value?.scenesByOrder?.get(sceneIndex)
            }
        }

        deviceOnline.value = false
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

    fun saveSceneAs(copyFrom: SceneWithComponents, copyTo: SceneWithComponents, newName: String) {
        if(copyFrom.scene.sceneId != copyTo.scene.sceneId) {
            copyTo.copyValues(copyFrom, newName)
        } else
            currentScene.mutation {
                it.value?.scene?.sceneName = newName
            }

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSceneOfCurrentPresetAs(copyTo)
        }
    }

//region scene/preset/output changed
    fun onSceneSelected(sceneIndex :Int) {
        Log.d("MPdebug", "scene $sceneIndex")
        sceneLoadedEvent.value = Event(sceneIndex)
    }

    fun onOutputSelected(outputIndex: Int) {
        Log.d("MPdebug", "output $outputIndex")
        currentOutput.value = outputIndex
    }
//endregion controls

//region mixer listeners
    fun onChannelChanged(audioChannel: AudioChannel) {
        currentState.value?.preset?.isDirty = true
        audioChannel.isDirty = true
        Log.d("MPdebug", "channel ${audioChannel.inputIndex} volume ${audioChannel.volume} panorama ${audioChannel.panorama} mute ${audioChannel.mute} solo ${audioChannel.solo}")
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
        Log.d("MPdebug", "channel $fxSend")
    }

    fun onFxReturnChanged(masterChannel: MasterChannel, fxReturn: Int) {
        currentState.value?.preset?.isDirty = true
        masterChannel.isDirty
        Log.d("MPdebug", "fxReturn $fxReturn")
    }

    fun onMasterVolumeChanged(masterChannel: MasterChannel) {
        currentState.value?.preset?.isDirty = true
        masterChannel.isDirty = true
        Log.d("MPdebug", "master volume ${masterChannel.volume}")
    }
//endregion mixer listeners
}