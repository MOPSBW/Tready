package com.securityapp.security.security.di;

/**
 * Created by Tyler on 2/18/2018.
 */

import com.securityapp.security.security.ui.login.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class LoginActivityModule {
    @ContributesAndroidInjector(modules = LoginFragmentBuildersModule.class)
    abstract LoginActivity contributeLoginActivityInjector();
}
