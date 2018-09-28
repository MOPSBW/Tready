package com.securityapp.security.security.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.securityapp.security.security.data.User;
import com.securityapp.security.security.service.network.Resource;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.request.LoginRequest;
import com.securityapp.security.security.service.request.TreadyCredential;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tyler on 1/11/2018.
 */
@Singleton
public class LoginRepository {

    private Webservice mWebservice;

    @Inject
    public LoginRepository(Webservice webservice){
        this.mWebservice = webservice;
    }

    public LiveData<Resource<User>> getLogin(TreadyCredential treadyCredential){
        MediatorLiveData<Resource<User>> userResponse = new MediatorLiveData<>();

        LoginRequest loginRequest = new LoginRequest(treadyCredential);

        userResponse.addSource(mWebservice.authenticateUser(loginRequest.getRequestParams()), user -> {
            if(user.isSuccessful()){
                userResponse.setValue(Resource.success(user.getData()));
            }else{
                userResponse.setValue(Resource.error(user.errorMessage, null));
            }
        });

        return userResponse;
    }
}