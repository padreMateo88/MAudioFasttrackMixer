package com.mpiotrowski.maudiofasttrackmixer.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CURRENT_PRESET_ID
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CURRENT_PRESET_NAME
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())
    private var currentOutput: Int = 1

    // mixer parameters
    var audioChannels: MutableLiveData<List<AudioChannel>> = MutableLiveData()
    var masterChannel: MutableLiveData<MasterChannel> = MutableLiveData()
    var fxSends: MutableLiveData<List<FxSend>> = MutableLiveData()
    //fx parameters
    var fxSettings: MutableLiveData<FxSettings> = MutableLiveData()
     //device parameters
    var sampleRate: MutableLiveData<SampleRate> = MutableLiveData()

    val allPresets = repository.presetsWithScenes
    var currentPreset: PresetWithScenes = PresetWithScenes.newInstance(Preset(CURRENT_PRESET_ID, CURRENT_PRESET_NAME))
    var currentScene: SceneWithComponents = currentPreset.scenes.sortedBy{ it.scene.sceneOrder }[0]

    var currentSceneName: MutableLiveData<String> = MutableLiveData()
    var currentPresetName: MutableLiveData<String> = MutableLiveData()
    var fine: MutableLiveData<Boolean> = MutableLiveData()

    init {
           currentSceneName.value = "Default scene"
           currentPresetName.value = "Default preset"
           viewModelScope.launch(Dispatchers.IO) {
               currentPreset = repository.getCurrentPreset()
               currentPreset.scenesByOrder[currentOutput]?.let { currentScene = it}
               viewModelScope.launch(Dispatchers.Main) {
                   onPresetLoaded()
               }
           }
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
            copyTo.scene.sceneName = newName
        if(copyTo.scene.sceneId == currentScene.scene.sceneId)
            currentSceneName.value = newName
        //TODO save to database
    }

    fun createPreset(presetName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentPreset.preset.presetName = presetName
            repository.addPreset(Preset(presetName = presetName))
        }
    }

    fun saveCurrentPresetAs(presetName: String): Boolean {
        return if(allPresets.value?.map { it.preset.presetName}?.contains(presetName) == true)
            false
        else {
            viewModelScope.launch(Dispatchers.IO) {
                currentPreset.preset.presetName = presetName
                repository.savePresetWithScenes(currentPreset, false)
            }
            true
        }
    }

    fun saveCurrentPresetAsExistingPreset(presetName: String) {
        val presetToRemove = allPresets.value?.map { it.preset.presetName to it }?.toMap()?.get(presetName)?.preset
        presetToRemove?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deletePreset(it)
                currentPreset.preset.presetName = presetName
                repository.savePresetWithScenes(currentPreset,false)
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
    private fun onPresetLoaded() {
        sampleRate.value = currentPreset.preset.sampleRate
        onSceneSelected(1)
    }

    fun onSceneSelected(sceneIndex :Int) {
        Log.d("MPdebug", "scene $sceneIndex")
        currentPreset.scenesByOrder[sceneIndex]?.let { currentScene = it }
        currentScene.channelsByOutputsMap[currentOutput].let{audioChannels.value = it}
        currentScene.mastersByOutputsMap[currentOutput].let{masterChannel.value = it}
        fxSends.value = currentScene.fxSends
        fxSettings.value = currentScene.scene.fxSettings
        currentSceneName.value = currentScene.scene.sceneName
    }

    fun onOutputSelected(outputIndex: Int) {
        Log.d("MPdebug", "output $outputIndex")
        currentOutput = outputIndex
        currentScene.channelsByOutputsMap[currentOutput].let{audioChannels.value = it}
        currentScene.mastersByOutputsMap[currentOutput].let{masterChannel.value = it}
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