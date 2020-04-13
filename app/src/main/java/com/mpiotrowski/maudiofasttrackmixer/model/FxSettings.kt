package com.mpiotrowski.maudiofasttrackmixer.model

import androidx.lifecycle.MutableLiveData

data class FxSettings(var fxType: MutableLiveData<FxType> = MutableLiveData(),
                      var duration: MutableLiveData<Int> = MutableLiveData(),
                      var feedback: MutableLiveData<Int> = MutableLiveData(),
                      var volume: MutableLiveData<Int> = MutableLiveData()) {
    init {
        fxType.value = FxType.ROOM1
        duration.value = 0
        feedback.value = 0
        volume.value = 0
    }
}