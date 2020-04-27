package com.mpiotrowski.maudiofasttrackmixer.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.Scene
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.*
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var audioChannels: MutableLiveData<MutableList<AudioChannel>> = MutableLiveData()
    var masterChannel: MutableLiveData<MasterChannel> = MutableLiveData()

    var fxSends: MutableLiveData<MutableList<FxSend>> = MutableLiveData()
    var fxSettings: FxSettings = FxSettings()
    var sampleRate: MutableLiveData<SampleRate> = MutableLiveData()

    var currentScene: MutableLiveData<Scene> = MutableLiveData()

    private val database = PresetsDatabase.getDatabase(getApplication(),viewModelScope)
    private val repository = Repository(database.presetsDao())

    init {
        audioChannels.value = mutableListOf(
            AudioChannel(inputNumber = 1, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 2, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 3, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 4, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 5, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 6, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 7, outputNumber = 1, sceneId = 0),
            AudioChannel(inputNumber = 8, outputNumber = 1, sceneId = 0)
        )

        val sceneId = currentScene.value?.sceneId ?: -1
        fxSends.value = mutableListOf(
            FxSend(channelNumber = 1, sceneId = sceneId),
            FxSend(channelNumber = 2, sceneId = sceneId),
            FxSend(channelNumber = 3, sceneId = sceneId),
            FxSend(channelNumber = 4, sceneId = sceneId),
            FxSend(channelNumber = 5, sceneId = sceneId),
            FxSend(channelNumber = 6, sceneId = sceneId),
            FxSend(channelNumber = 7, sceneId = sceneId),
            FxSend(channelNumber = 8, sceneId = sceneId)
        )

        masterChannel.value =
            MasterChannel(sceneId = 0, outputNumber = 1)

        sampleRate.value = SampleRate.SR_96

        currentScene.value = Scene(sceneName = "default scene's name",fxSettings = fxSettings, presetId = "",sceneOrder = 0)

        repository.presetsWitScenes.observeForever {
            Log.d("MPdebug", "presets fetched")
        }
    }

    fun insertDefaultPreset() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPreset("defaultPreset")
        }
    }

    fun onSceneSelected(sceneIndex :Int) {
        Log.d("MPdebug", "scene $sceneIndex")
    }

    fun onChannelChanged(audioChannel: AudioChannel) {
        Log.d("MPdebug", "channel ${audioChannel.inputNumber} volume ${audioChannel.volume} panorama ${audioChannel.panorama} mute ${audioChannel.mute} solo ${audioChannel.solo}")
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
//region Fx listeners
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
//endRegion FX listeners
}