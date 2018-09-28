package com.securityapp.security.security;

import android.app.Application;

/**
 * Use different App for testings so Dagger dependency injection is not initialized
 */

public class TestApp extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
    }
}
