package com.securityapp.security.security.di;

import com.securityapp.security.security.ui.login.LoginFragment;
import com.securityapp.security.security.ui.setup.SetupFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Tyler on 2/26/2018.
 */

@Module
abstract class LoginFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract LoginFragment constributeLoginFragment();

    @ContributesAndroidInjector
    abstract SetupFragment contributeSetupFragment();
}
