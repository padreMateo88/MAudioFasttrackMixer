package com.mpiotrowski.maudiofasttrackmixer

import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MAudioFasttrackMixerApplication : DaggerApplication() {

    lateinit var database: PresetsDatabase
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        database = PresetsDatabase.getDatabase(this, CoroutineScope(Dispatchers.Default))
        repository = Repository(database.presetsDao())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this@MAudioFasttrackMixerApplication)
    }
}