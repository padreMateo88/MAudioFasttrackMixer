package com.mpiotrowski.maudiofasttrackmixer.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*
import com.mpiotrowski.maudiofasttrackmixer.util.Event
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())

    private val sceneLoadedEvent = MutableLiveData<Event<Int>>()
    private val currentOutput: MutableLiveData<Int> = MutableLiveData()

    var fine:  MutableLiveData<Boolean> = MutableLiveData()
    var deviceOnline:  MutableLiveData<Boolean> = MutableLiveData()

    var allPresets: LiveData<List<PresetWithScenes>> = repository.presetsWithScenes
    var currentPreset = repository.currentPreset

    var currentState = repository.currentState
    var currentScene = MediatorLiveData<SceneWithComponents>()

    var audioChannels = MediatorLiveData<List<AudioChannel>>()
    var masterChannel = MediatorLiveData<MasterChannel>()
    var fxSends = MediatorLiveData<List<FxSend>>()

    var fxSettings = MutableLiveData<FxSettings>()
    var sampleRate = MutableLiveData<SampleRate>()

    init {
        audioChannels.addSource(currentOutput) { outputIndex ->
            Log.d("MPdebug", "audioChannels set ${currentOutput.value}")
            audioChannels.value = currentScene.value?.channelsByOutputsMap?.get(outputIndex)
        }

        audioChannels.addSource(currentScene) { sceneWithComponents ->
            Log.d("MPdebug", "audioChannels set ${currentScene.value}")
            audioChannels.value = sceneWithComponents.channelsByOutputsMap[currentOutput.value]
        }

        masterChannel.addSource(currentOutput) { outputIndex ->
            masterChannel.value = currentScene.value?.mastersByOutputsMap?.get(outputIndex)
        }

        masterChannel.addSource(currentScene) { sceneWithComponents ->
            masterChannel.value = sceneWithComponents.mastersByOutputsMap[currentOutput.value]
        }

        fxSends.addSource(currentScene) { sceneWithComponents ->
            fxSends.value = sceneWithComponents.fxSends
        }

        currentScene.addSource(currentState) { value ->
            currentScene.value = value?.scenes?.get(1)
        }

        currentScene.addSource(sceneLoadedEvent) { sceneSelectedEvent ->
            sceneSelectedEvent.getContentIfNotHandled()?.let {
                sceneIndex -> currentScene.value = currentState.value?.scenesByOrder?.get(sceneIndex)
            }
        }

        deviceOnline.value = false
        currentOutput.value = 1
    }

// region save/load preset

    fun loadPreset(presetWithScenes: PresetWithScenes) {
        Log.d("MPdebug", "loadPreset ${presetWithScenes.preset.presetName}")
        //TODO
    }

    fun removePreset(presetWithScenes: PresetWithScenes) {
        Log.d("MPdebug", "removePreset ${presetWithScenes.preset.presetName}")
        //TODO
    }

    fun saveSceneAs(copyFrom: SceneWithComponents, copyTo: SceneWithComponents, newName: String) {
        if(copyFrom.scene.sceneId != copyTo.scene.sceneId)
            copyTo.copyValues(copyFrom,newName)
        else
            currentScene.mutation {
                it.value?.scene?.sceneName = newName
            }
        //TODO save to database
    }

    fun createPreset(presetName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPreset(Preset(presetName = presetName))
        }
    }

    fun saveCurrentPresetAs(presetName: String): Boolean {
        return if(allPresets.value?.map { it.preset.presetName}?.contains(presetName) == true)
            false
        else {
            viewModelScope.launch(Dispatchers.IO) {
                currentState.value?.preset?.presetName = presetName
                currentState.value?.let {currentPreset -> repository.savePresetWithScenes(currentPreset, false)}
            }
            true
        }
    }

    fun saveCurrentPresetAsExistingPreset(presetName: String) {
        val presetToRemove = allPresets.value?.map { it.preset.presetName to it }?.toMap()?.get(presetName)?.preset
        presetToRemove?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deletePreset(it)
                currentState.value?.let{
                    currentPreset ->
                        currentPreset.preset.presetName = presetName
                        repository.savePresetWithScenes(currentPreset, false)
                }
            }
        }
    }

    fun swapScenesInPreset(presetWithScenes: PresetWithScenes, fromOrder: Int, toOrder: Int) {
        val sceneFrom = presetWithScenes.scenesByOrder[fromOrder]
        val sceneTo = presetWithScenes.scenesByOrder[toOrder]

        sceneFrom?.scene?.sceneOrder = toOrder
        sceneTo?.scene?.sceneOrder = fromOrder
        sceneFrom?.let{presetWithScenes.scenesByOrder.put(toOrder, it)}
        sceneTo?.let{presetWithScenes.scenesByOrder.put(fromOrder, it)}
    }
// endregion save/load preset

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
        Log.d("MPdebug", "channel $fxSend")
    }

    fun onFxReturnChanged(fxReturn: Int) {
        Log.d("MPdebug", "fxReturn $fxReturn")
    }

    fun onMasterVolumeChanged(masterChannel: MasterChannel) {
        Log.d("MPdebug", "master volume $masterChannel")
    }
//endregion mixer listeners

//region FX listeners
    fun onFxVolumeChanged(fxVolume: Int) {
        Log.d("MPdebug", "fxVolume $fxVolume")
    }

    fun onFxDurationChanged(fxDuration: Int) {
        Log.d("MPdebug", "fxDuration $fxDuration")
    }

    fun onFxFeedbackChanged(fxFeedback: Int) {
        Log.d("MPdebug", "fxFeedback $fxFeedback")
    }

    fun onFxTypeChanged(fxType: FxSettings.FxType) {
        Log.d("MPdebug", "fxType $fxType")
    }

    fun onSampleRateChanged(sampleRate: SampleRate) {
        Log.d("MPdebug", "sampleRate $sampleRate")
    }
//endregion FX listeners
}