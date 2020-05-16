package com.mpiotrowski.maudiofasttrackmixer.ui.presets

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

class PresetsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())

    lateinit var currentPresetId: String
    val allPresets: LiveData<List<PresetWithScenes>> = repository.presetsWithScenes
    val currentState = MediatorLiveData<PresetWithScenes>()
    val selectedPreset = MediatorLiveData<PresetWithScenes>()

    init {
        fetchCurrentPresetId()

        currentState.addSource(repository.currentState) {
            if(currentState.value == null)
                currentState.value = repository.currentState.value
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
    }

    fun getSelectedPresetScenes(): Map<Int, SceneWithComponents>? {
        return selectedPreset.value?.scenesByOrder
    }

    fun getAllPresets(): List<PresetWithScenes>? {
        return allPresets.value
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
}