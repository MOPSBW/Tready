package com.securityapp.security.security.service.network;

import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.utils.LiveDataCallAdapterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class WebserviceProvider implements Injectable{
    private static WebserviceProvider sProvider;
    private Webservice mWebservice;
    private String baseUrl = null;
    private OkHttpClient okHttpClient;

    private WebserviceProvider(OkHttpClient okHttpClient){
        this.okHttpClient = okHttpClient;
        updateRetrofit();
    }

    public static WebserviceProvider getInstance(OkHttpClient okHttpClient){
        if(sProvider == null){
            sProvider = new WebserviceProvider(okHttpClient);
        }
        return sProvider;
    }

    private void setBaseUrl(String url){
        this.baseUrl = url;
    }

    public void updateRetrofit(){
        if(baseUrl == null){
            baseUrl = "http://localhost.com";
        }
        this.mWebservice = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(Webservice.class);
    }

    public void updateRetrofitURL(String baseUrl){
        this.setBaseUrl(baseUrl);
        this.updateRetrofit();
    }

    public Webservice getService(){
        return this.mWebservice;
    }
}
