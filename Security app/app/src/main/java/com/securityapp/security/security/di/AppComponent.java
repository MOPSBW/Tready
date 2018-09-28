package com.securityapp.security.security.di;

import android.app.Application;

import com.securityapp.security.security.TreadyApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Tyler on 2/18/2018.
 */

@Singleton
@Component(modules = {
        AppModule.class,
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        LoginActivityModule.class,
        MainActivityModule.class,
})
public interface AppComponent {

    @Component.Builder
    interface Builder{
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }

    void inject(TreadyApp treadyApp);
}