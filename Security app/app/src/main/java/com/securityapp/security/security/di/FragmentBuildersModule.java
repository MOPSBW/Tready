package com.securityapp.security.security.di;

import com.securityapp.security.security.ui.device.DeviceFragment;
import com.securityapp.security.security.ui.event.EventFragment;
import com.securityapp.security.security.ui.eventVideo.EventVideoFragment;
import com.securityapp.security.security.ui.settings.SettingsFragment;
import com.securityapp.security.security.ui.setup.SetupFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Tyler on 2/18/2018.
 */

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract EventFragment contributeEventFragment();

    @ContributesAndroidInjector
    abstract EventVideoFragment contributeEventVideoFragment();

    @ContributesAndroidInjector
    abstract DeviceFragment contributeDeviceFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();
}