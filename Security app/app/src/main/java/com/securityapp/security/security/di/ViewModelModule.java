package com.securityapp.security.security.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.securityapp.security.security.ui.device.DeviceViewModel;
import com.securityapp.security.security.ui.event.EventViewModel;
import com.securityapp.security.security.ui.login.LoginViewModel;
import com.securityapp.security.security.viewmodel.TreadyViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Tyler on 2/18/2018.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    abstract ViewModel bindEventViewModel(EventViewModel eventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeviceViewModel.class)
    abstract ViewModel bindDeviceViewModel(DeviceViewModel deviceViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(TreadyViewModelFactory treadyViewModelFactory);
}
