package com.mpiotrowski.maudiofasttrackmixer.di

import androidx.lifecycle.ViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class,
        FragmentsModule::class
    ])
    internal abstract fun contributeMainActivity() : MainActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindViewModel(mainViewModel: MainViewModel): ViewModel
}