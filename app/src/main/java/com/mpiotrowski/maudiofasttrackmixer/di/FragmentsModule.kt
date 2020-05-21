package com.mpiotrowski.maudiofasttrackmixer.di

import androidx.lifecycle.ViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.mixer.MixerFragment
import com.mpiotrowski.maudiofasttrackmixer.ui.mixer.MixerViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetsFragment
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetsViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.settings.SettingsFragment
import com.mpiotrowski.maudiofasttrackmixer.ui.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun contributeMixerFragment() : MixerFragment

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun contributeSettingsFragment() : SettingsFragment

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun contributePresetsFragment() : PresetsFragment

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PresetsViewModel::class)
    internal abstract fun bindPresetsViewModel(presetsViewModel: PresetsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MixerViewModel::class)
    internal abstract fun bindMixerViewModel(mixerViewModel: MixerViewModel): ViewModel
}