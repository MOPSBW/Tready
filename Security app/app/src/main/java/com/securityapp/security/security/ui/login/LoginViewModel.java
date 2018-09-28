package com.securityapp.security.security.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.securityapp.security.security.data.User;
import com.securityapp.security.security.repository.LoginRepository;
import com.securityapp.security.security.service.network.Status;
import com.securityapp.security.security.service.request.TreadyCredential;
import com.securityapp.security.security.utils.CredentialService;

import javax.inject.Inject;

/**
 * Created by Tyler on 1/11/2018.
 */

public class LoginViewModel extends ViewModel {
    private LoginRepository mLoginRepository;

    private CredentialService mCredentialService;

    private final MediatorLiveData<User> mUser = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> mIsBusy = new MutableLiveData<>();

    @Inject
    public LoginViewModel(LoginRepository loginRepository, CredentialService credentialService){
        mLoginRepository = loginRepository;
        mCredentialService = credentialService;

        if(mCredentialService.getUsername() != null){
            loginUser(credentialService.getUsername(), credentialService.getUserPassword(), false);
        }else{
            mCredentialService.removeAllKeyStorePairs();
        }
    }

    public LiveData<User> getUser(){
        return mUser;
    }

    LiveData<Boolean> getIsBusy(){
        return mIsBusy;
    }

    void loginUser(String username, String password, boolean saveUser){
        mIsBusy.setValue(true);
        mUser.addSource(
                mLoginRepository.getLogin(new TreadyCredential(username, password)),
                userResource -> {
                    if(userResource.status == Status.SUCCESS){
                        if(saveUser){
                            mCredentialService.createNewKeys(username);
                            mCredentialService.storeUserCredentials(username, password);
                        }
                        mIsBusy.setValue(false);
                        mUser.setValue(userResource.data);
                    }else{
                        mUser.setValue(null);
                    }
                    mIsBusy.postValue(false);
                }
        );
    }
}