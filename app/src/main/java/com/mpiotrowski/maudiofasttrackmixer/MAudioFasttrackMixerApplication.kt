package com.mpiotrowski.maudiofasttrackmixer

import com.mpiotrowski.maudiofasttrackmixer.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MAudioFasttrackMixerApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this@MAudioFasttrackMixerApplication)
    }
}