package com.mpiotrowski.maudiofasttrackmixer.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mpiotrowski.maudiofasttrackmixer.model.AudioChannel

class MainViewModel : ViewModel() {

    val audioChannels = MutableLiveData<List<AudioChannel>>()

    init {
        audioChannels.value = mutableListOf(
            AudioChannel(0,1),
            AudioChannel(0,2),
            AudioChannel(0,3),
            AudioChannel(0,4),
            AudioChannel(0,5),
            AudioChannel(0,6),
            AudioChannel(0,7),
            AudioChannel(0,8)
        )
    }

    fun onVolumeChanged(audioChannel: AudioChannel){
        Log.d("MPdebug", "channel ${audioChannel.channelId}")
    }
}