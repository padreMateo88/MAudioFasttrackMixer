package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.usb.UsbController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PresetsViewModel @Inject constructor(private val repository: Repository, private var usbController: UsbController) : ViewModel()  {

    lateinit var currentPresetId: String
    val allPresets: LiveData<List<PresetWithScenes>> = repository.presetsWithScenes
    val currentState = repository.currentModelState
    val selectedPreset = MediatorLiveData<PresetWithScenes>()

    init {
        fetchCurrentPresetId()

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

    fun selectPreset(index: Int) {
        allPresets.value?.get(index)?.let {
            selectedPreset.value = it
        }
    }

    fun loadPreset(presetToLoad: PresetWithScenes) {
        repository.setCurrentPreset(presetToLoad)
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
            currentState.value?.preset?.isDirty = false
            repository.notifyCurrentStateChanged()
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
            currentStateValue.preset.isDirty = false
            currentStateValue.preset.presetName = presetName
            repository.notifyCurrentStateChanged()
            viewModelScope.launch(Dispatchers.IO) {
                repository.saveCurrentPreset(currentStateValue)
            }
        }
    }

    fun saveCurrentPresetAsExisting(presetName: String) {
         val destinationPreset = allPresets.value?.map {it.preset.presetName to it}?.toMap()?.get(presetName) ?: return

        currentState.value?.let { currentStateValue ->
            currentStateValue.preset.isDirty = false
            currentStateValue.preset.presetName = presetName
            repository.notifyCurrentStateChanged()
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
            currentState.value?.preset?.isDirty = false
            currentState.value?.preset?.presetName = presetName
            repository.notifyCurrentStateChanged()
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
            if(currentPresetId == it.preset.presetId) {
                currentState.value?.let { currentStateValue ->
                    swapScenesInPreset(currentStateValue, fromOrder, toOrder)
                    repository.notifyCurrentStateChanged()
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
}