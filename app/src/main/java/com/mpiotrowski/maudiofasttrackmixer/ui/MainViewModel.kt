package com.mpiotrowski.maudiofasttrackmixer.ui

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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())

    lateinit var currentPresetId: String
    val allPresets: LiveData<List<PresetWithScenes>> = repository.presetsWithScenes
    val currentState = MediatorLiveData<PresetWithScenes>()
    private val allScenes = MediatorLiveData<List<Scene>>()
    private val sceneLoadedEvent = MutableLiveData<Event<Int>>()
    private val currentOutput = MutableLiveData<Int>()
    val selectedPreset = MediatorLiveData<PresetWithScenes>()
    val currentScene = MediatorLiveData<SceneWithComponents>()
    val audioChannels = MediatorLiveData<List<AudioChannel>>()
    val masterChannel = MediatorLiveData<MasterChannel>()
    val fxSends = MediatorLiveData<List<FxSend>>()
    val fxSettings = MediatorLiveData<FxSettings>()
    val sampleRate = MediatorLiveData<SampleRate>()
    val fine = MutableLiveData<Boolean>()
    val deviceOnline = MutableLiveData<Boolean>()

    init {
        fetchCurrentPresetId()

        allScenes.addSource(repository.presetsWithScenes) {
            Log.d("MPdebug", "scenes ${it.size}")
        }

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

        selectedPreset.addSource(allPresets) { allPresetsListNullable ->
            allPresetsListNullable?.let {allPresets ->
                if(selectedPreset.value == null && allPresets.isNotEmpty()) {
                    selectedPreset.value = allPresets[0]
                }

                selectedPreset.value?.let {
                    if(!allPresets.contains(it)){
                        selectedPreset.value = allPresets[0]
                    }
                }
            }
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

    fun getSelectedPresetScenes(): Map<Int, SceneWithComponents>? {
        return selectedPreset.value?.scenesByOrder
    }

    fun getAllPresets(): List<PresetWithScenes>? {
        return allPresets.value
    }

    fun getSceneOfCurrentState(order: Int): SceneWithComponents? {
        return currentState.value?.scenesByOrder?.get(order)
    }

    fun getCurrentState(): PresetWithScenes? {
        return currentState.value
    }

    fun isCurrentStateDirty(): Boolean {
        return currentState.value?.preset?.isDirty == true
    }

    fun getCurrentPresetName(): String? {
        return currentState.value?.preset?.presetName
    }

    fun getCurrentSceneOrder(): Int? {
        return currentScene.value?.scene?.sceneOrder
    }

    fun getAudioChannelsNumber(): Int {
        return audioChannels.value?.size ?: 0
    }

    private fun fetchCurrentPresetId() {
        viewModelScope.launch(Dispatchers.IO) {
            currentPresetId = repository.getCurrentPresetId()
        }
    }

    // region save/load preset
    fun saveCurrentDeviceState() {
        viewModelScope.launch(Dispatchers.IO) {
            currentState.value?.let {
                Log.d("MPdebug", "save current device state")
                repository.savePresetWithScenes(it, false)
            }
        }
    }

    fun selectPreset(index: Int) {
        allPresets.value?.get(index)?.let {
            selectedPreset.value = it
        }
    }

    fun loadPreset(presetToLoad: PresetWithScenes) {
        currentState.mutation {
            it.value?.copyValues(presetToLoad, presetToLoad.preset.presetName)
        }

        currentPresetId = presetToLoad.preset.presetId
        viewModelScope.launch(Dispatchers.IO) {
                repository.saveCurrentPresetId(presetToLoad)
        }
    }

    fun removePreset(presetWithScenes: PresetWithScenes) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePreset(presetWithScenes.preset)
        }
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

    fun createPreset(presetName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPreset(Preset(presetName = presetName))
        }
    }

    fun saveCurrentPreset(presetName: String): Boolean {
        return if(presetName != currentState.value?.preset?.presetName) {
            false
        } else {
            currentState.mutation {
                it.value?.preset?.isDirty = false
            }
            viewModelScope.launch(Dispatchers.IO) {
                currentState.value?.let { currentStateValue ->
                    repository.saveCurrentPreset(currentStateValue)
                }
            }
            true
        }
    }

    fun saveAndRenameCurrentPreset(presetName: String) {
        currentState.value?.let { currentStateValue ->
            currentState.mutation {
                currentStateValue.preset.isDirty = false
                currentStateValue.preset.presetName = presetName
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveCurrentPreset(currentStateValue)
            }
        }
    }

    fun saveCurrentPresetAsExisting(presetName: String) {
         val destinationPreset = allPresets.value?.map {it.preset.presetName to it}?.toMap()?.get(presetName) ?: return

        currentState.value?.let { currentStateValue ->
            currentState.mutation {
                currentStateValue.preset.isDirty = false
                currentStateValue.preset.presetName = presetName
            }
            viewModelScope.launch(Dispatchers.IO) {
                destinationPreset.copyValues(currentStateValue, presetName)
                repository.savePresetWithScenes(currentStateValue, false)
                repository.savePresetWithScenes(destinationPreset, true)
                repository.saveCurrentPresetId(destinationPreset)
                fetchCurrentPresetId()
            }
        }
    }

    fun saveCurrentPresetAsNewPreset(presetName: String) {
        if(allPresets.value?.map {it.preset.presetName}?.contains(presetName) == true) {
            return
        } else {
            currentState.mutation {
                it.value?.preset?.isDirty = false
                it.value?.preset?.presetName = presetName
            }
            viewModelScope.launch(Dispatchers.IO) {
                currentState.value?.let { currentState ->
                        val newPreset = PresetWithScenes.newInstance(Preset(presetName = presetName))
                        newPreset.copyValues(currentState, presetName)
                        repository.addPresetWithScenes(newPreset)
                        repository.savePresetWithScenes(currentState, false)
                        repository.saveCurrentPresetId(newPreset)
                        fetchCurrentPresetId()
                }
            }
        }
    }

    fun swapScenesInSelectedPresetAndCurrentState(fromOrder: Int, toOrder: Int) {
        selectedPreset.value?.let {
            swapScenesInPreset(it, fromOrder, toOrder)
            if(currentPresetId == it.preset.presetId)
                currentState.mutation {
                currentState.value?.let {currentStateValue ->
                    swapScenesInPreset(currentStateValue, fromOrder, toOrder)
                }
            }
        }
    }

    private fun swapScenesInPreset(presetWithScenes: PresetWithScenes, fromOrder: Int, toOrder: Int) {
        val sceneFrom = presetWithScenes.scenesByOrder[fromOrder]
        val sceneTo = presetWithScenes.scenesByOrder[toOrder]

        sceneFrom?.scene?.sceneOrder = toOrder
        sceneTo?.scene?.sceneOrder = fromOrder

        sceneFrom?.let{presetWithScenes.scenesByOrder.put(toOrder, it)}
        sceneTo?.let{presetWithScenes.scenesByOrder.put(fromOrder, it)}

        viewModelScope.launch(Dispatchers.IO) {
            sceneFrom?.let {repository.saveScene(it.scene)}
            sceneTo?.let {repository.saveScene(it.scene)}
        }
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