package com.mpiotrowski.maudiofasttrackmixer.di

import android.content.Context
import com.mpiotrowski.maudiofasttrackmixer.MAudioFasttrackMixerApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    MainActivityModule::class,
    AppModule::class
])
interface ApplicationComponent: AndroidInjector<MAudioFasttrackMixerApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}