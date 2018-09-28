package com.securityapp.security.security;

import android.app.Activity;
import android.app.Application;

import com.securityapp.security.security.di.AppInjector;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by Tyler on 2/18/2018.
 */

public class TreadyApp extends Application implements HasActivityInjector{

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);

        //used for identifying and debugging memory leaks
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }
}