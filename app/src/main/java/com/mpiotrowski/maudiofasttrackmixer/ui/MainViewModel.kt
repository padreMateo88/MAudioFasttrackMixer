package com.mpiotrowski.maudiofasttrackmixer.ui

import androidx.lifecycle.*
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val currentState = repository.currentModelState

    val deviceConnected = MutableLiveData<Boolean>()
    val deviceConfigured = MutableLiveData<Boolean>()

    init {
        deviceConnected.value = false
        deviceConfigured.value = false
    }

    fun saveCurrentDeviceState() {
        viewModelScope.launch(Dispatchers.IO) {
            currentState.value?.let {
                repository.savePresetWithScenes(it, true)
            }
        }
    }
}