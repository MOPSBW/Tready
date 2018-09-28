package com.securityapp.security.security.repository;

import android.support.annotation.NonNull;

import com.securityapp.security.security.data.User;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.request.LoginRequest;
import com.securityapp.security.security.service.request.TreadyCredential;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tyler on 3/8/2018.
 */

@Singleton
public class ReauthRepository {

    private Webservice mWebservice;

    public interface ReauthRepoListener{
        void reauthResults(boolean isValid);
    }

    @Inject
    public ReauthRepository(Webservice webservice){
        mWebservice = webservice;
    }

    public void reauthRequest(TreadyCredential treadyCredential, ReauthRepoListener listener){
        LoginRequest loginRequest = new LoginRequest(treadyCredential);
        Call<User> call = mWebservice.reauthUser(loginRequest.getRequestParams());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful() ){
                    if(response.body().isUserValid()){
                        listener.reauthResults(true);
                    }else{
                        listener.reauthResults(false);
                    }
                }else{
                    listener.reauthResults(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                listener.reauthResults(false);
            }
        });
    }
}