package com.mpiotrowski.maudiofasttrackmixer.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mpiotrowski.maudiofasttrackmixer.model.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.model.FxSend
import com.mpiotrowski.maudiofasttrackmixer.model.MasterChannel
import com.mpiotrowski.maudiofasttrackmixer.util.mutation

class MainViewModel : ViewModel() {

    var audioChannels: MutableLiveData<MutableList<AudioChannel>> = MutableLiveData()
    var masterChannel: MutableLiveData<MasterChannel> = MutableLiveData()
    var fxSends: MutableLiveData<MutableList<FxSend>> = MutableLiveData()

    init {
        audioChannels.value = mutableListOf(
            AudioChannel(channelId = 1),
            AudioChannel(channelId = 2),
            AudioChannel(channelId = 3),
            AudioChannel(channelId = 4),
            AudioChannel(channelId = 5),
            AudioChannel(channelId = 6),
            AudioChannel(channelId = 7),
            AudioChannel(channelId = 8)
        )

        fxSends.value = mutableListOf(
            FxSend(channelId = 1),
            FxSend(channelId = 2),
            FxSend(channelId = 3),
            FxSend(channelId = 4),
            FxSend(channelId = 5),
            FxSend(channelId = 6),
            FxSend(channelId = 7),
            FxSend(channelId = 8)
        )

        masterChannel.value = MasterChannel()
    }

    fun onSceneSelected(sceneIndex :Int) {
        Log.d("MPdebug", "scene $sceneIndex")
    }

    fun onChannelChanged(audioChannel: AudioChannel) {
        Log.d("MPdebug", "channel ${audioChannel.channelId} volume ${audioChannel.volume} panorama ${audioChannel.panorama} mute ${audioChannel.mute} solo ${audioChannel.solo}")
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
        Log.d("MPdebug", "channel ${fxSend.channelId} fxSend ${fxSend.volume}")
    }

    fun onFxReturnChanged(fxReturn: Int) {
        Log.d("MPdebug", "fxReturn $fxReturn")
    }

    fun onMasterVolumeChanged(masterChannel: MasterChannel) {
        Log.d("MPdebug", "master volume ${masterChannel.volume} mute ${masterChannel.mute}")
    }
}