package com.mpiotrowski.maudiofasttrackmixer.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mpiotrowski.maudiofasttrackmixer.model.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.model.FxSend

class MainViewModel : ViewModel() {

    val audioChannels = MutableLiveData<List<AudioChannel>>()
    val fxSends = MutableLiveData<List<FxSend>>()

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
    }

    fun onChannelChanged(audioChannel: AudioChannel) {
        Log.d("MPdebug", "channel ${audioChannel.channelId} volume ${audioChannel.volume} + panorama ${audioChannel.panorama}")
    }

    fun onFxSendChanged(fxSend: FxSend) {
        Log.d("MPdebug", "channel ${fxSend.channelId} fxSend ${fxSend.volume}")
    }
}