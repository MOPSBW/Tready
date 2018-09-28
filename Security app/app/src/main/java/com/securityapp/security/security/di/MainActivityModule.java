package com.securityapp.security.security.di;

import com.securityapp.security.security.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Tyler on 2/18/2018.
 */

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeEventActivity();
}