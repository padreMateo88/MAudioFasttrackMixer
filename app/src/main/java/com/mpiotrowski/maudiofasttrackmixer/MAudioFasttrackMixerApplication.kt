package com.mpiotrowski.maudiofasttrackmixer

import android.app.Application
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MAudioFasttrackMixerApplication : Application() {

    lateinit var database: PresetsDatabase
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        database = PresetsDatabase.getDatabase(this, CoroutineScope(Dispatchers.Default))
        repository = Repository(database.presetsDao())
    }
}