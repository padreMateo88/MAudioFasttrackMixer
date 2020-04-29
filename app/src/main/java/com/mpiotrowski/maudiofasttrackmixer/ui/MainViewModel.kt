package com.mpiotrowski.maudiofasttrackmixer.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
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
    var fxType: MutableLiveData<FxSettings.FxType> = MutableLiveData()
    var duration: MutableLiveData<Int> = MutableLiveData()
    var feedback: MutableLiveData<Int> = MutableLiveData()
    var volume: MutableLiveData<Int> = MutableLiveData()


     //device parameters
    var sampleRate: MutableLiveData<SampleRate> = MutableLiveData()

    val allPresets = repository.presetsWithScenes
    private lateinit var currentPreset: PresetWithScenes
    var currentScene: SceneWithComponents? = null

    init {
       viewModelScope.launch(Dispatchers.IO) {
           currentPreset = repository.getCurrentPreset()
           currentScene = currentPreset.scenesByOrder[currentOutput]

           viewModelScope.launch(Dispatchers.Main) {
               onPresetLoaded()
           }
       }
    }
//region scene/preset/output changed
    private fun onPresetLoaded() {
        sampleRate.value = currentPreset.preset.sampleRate
        onSceneSelected(1)
    }

    fun onSceneSelected(sceneIndex :Int) {
        Log.d("MPdebug", "scene $sceneIndex")
        currentScene = currentPreset.scenesByOrder[sceneIndex]

        currentScene?.channelsByOutputsMap?.get(currentOutput).let{audioChannels.value = it}
        currentScene?.mastersByOutputsMap?.get(currentOutput).let{masterChannel.value = it}
        currentScene?.fxSends?.let{fxSends.value = it}

        currentScene?.scene?.fxSettings?.let {
            fxType.value = it.fxType
            duration.value = it.duration
            feedback.value = it.feedback
            volume.value = it.volume
        }
    }

    fun onOutputSelected(outputIndex: Int) {
        Log.d("MPdebug", "output $outputIndex")
        currentOutput = outputIndex
        currentScene?.channelsByOutputsMap?.get(currentOutput).let{audioChannels.value = it}
        currentScene?.mastersByOutputsMap?.get(currentOutput).let{masterChannel.value = it}
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