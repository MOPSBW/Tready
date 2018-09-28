package com.securityapp.security.security.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.securityapp.security.security.TreadyApp;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Tyler on 2/18/2018.
 */

public class AppInjector {
    private AppInjector(){}

    public static void init(TreadyApp treadyApp){
        DaggerAppComponent.builder().application(treadyApp)
                .build()
                .inject(treadyApp);

        treadyApp.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                handleActivity(activity);
            }
            //region Override methods
            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
            //endregion
        });
    }

    public static void handleActivity(Activity activity){
        if(activity instanceof HasSupportFragmentInjector){
            AndroidInjection.inject(activity);
        }
        if(activity instanceof FragmentActivity){
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(
                            new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                                    if(f instanceof Injectable){
                                        AndroidSupportInjection.inject(f);
                                    }
                                }
                            }, true);
        }
    }
}
