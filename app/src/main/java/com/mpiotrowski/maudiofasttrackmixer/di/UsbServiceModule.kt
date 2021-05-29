package com.mpiotrowski.maudiofasttrackmixer.di

import com.mpiotrowski.maudiofasttrackmixer.usb.UsbService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UsbServiceModule {

    @ContributesAndroidInjector()
    internal abstract fun contributeUsbService(): UsbService

}