package com.mpiotrowski.maudiofasttrackmixer.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.MAudioFasttrackMixerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MAudioFasttrackMixerApplication).repository
    private val currentState = repository.currentState

    fun saveCurrentDeviceState() {
        viewModelScope.launch(Dispatchers.IO) {
            currentState.value?.let {
                repository.savePresetWithScenes(it, true)
            }
        }
    }
}