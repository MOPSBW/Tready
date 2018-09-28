package com.securityapp.security.security.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.network.WebserviceProvider;
import com.securityapp.security.security.utils.CredentialService;
import com.securityapp.security.security.utils.LiveDataCallAdapterFactory;
import com.securityapp.security.security.utils.SharedPreferencesManager;

import java.net.CookieHandler;
import java.net.CookieManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tyler on 2/18/2018.
 */

@Module(includes = ViewModelModule.class)
class AppModule {
    private String baseUrl = "http://astralqueen.bw.edu/skunkworks/";

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application){
        return application.getSharedPreferences("Tready", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    CredentialService provideCredentialService(Application application, SharedPreferencesManager sharedPreferencesManager){
        return new CredentialService(application.getApplicationContext(), sharedPreferencesManager);
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application){
        int cacheSize = 10 * 1024 * 1024; //10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        return new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.NONE);
    }

    @Provides
    @Singleton
    CookieManager provideCookieManager() {
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        return manager;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache, HttpLoggingInterceptor httpLoggingInterceptor, CookieManager cookieManager){
        return new OkHttpClient.Builder()
                .cache(cache)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    WebserviceProvider provideWebserviceProvider(OkHttpClient okHttpClient){
        return WebserviceProvider.getInstance(okHttpClient);
    }

    @Provides
    @Singleton
    Webservice provideWebservice(WebserviceProvider provider){
        return provider.getService();
    }
}