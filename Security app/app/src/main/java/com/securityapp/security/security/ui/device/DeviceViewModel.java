package com.securityapp.security.security.ui.device;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.securityapp.security.security.R;
import com.securityapp.security.security.data.Device;
import com.securityapp.security.security.repository.DeviceRepository;
import com.securityapp.security.security.service.network.Status;
import com.securityapp.security.security.service.request.DeviceConfigurationRequest;
import com.securityapp.security.security.utils.SingleLiveEvent;
import com.securityapp.security.security.utils.SnackbarMessage;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tyler on 1/20/2018.
 */

public class DeviceViewModel extends ViewModel {
    private MediatorLiveData<List<Device>> mDevices;
    private DeviceRepository mDeviceRepository;
    private final SnackbarMessage mSnackbarMessage = new SnackbarMessage();
    private final SingleLiveEvent<Boolean> mReauthRequired = new SingleLiveEvent<>();

    @Inject
    public DeviceViewModel(DeviceRepository deviceRepository){
        mDeviceRepository = deviceRepository;

        if(mDevices == null){
            mDevices = new MediatorLiveData<>();
        }
        loadDevices();
    }

    public LiveData<List<Device>> getDevices(){
        return mDevices;
    }

    public SnackbarMessage getSnackbarMessage(){
        return mSnackbarMessage;
    }

    public SingleLiveEvent<Boolean> reauthRequired(){
        return mReauthRequired;
    }

    public void setDeviceConfiguration(DeviceConfigurationRequest configRequest){
        mDevices.addSource(mDeviceRepository.setDeviceConfig1(configRequest), deviceConfigResponseResource -> {
            switch(deviceConfigResponseResource.status){
                case SUCCESS:
                    loadDevices();
                    break;
                case SESSION_EXPIRED:
                    mReauthRequired.setValue(true);
                    break;
                case ERROR:
                    mSnackbarMessage.setValue(R.string.networkErrorMsg);
                    break;
            }
        });
    }

    public void loadDevices(){
        mDevices.addSource(
                mDeviceRepository.getDevices(), deviceResponseResource -> {
                    switch(deviceResponseResource.status){
                        case SUCCESS:
                            mDevices.setValue(deviceResponseResource.data.getResponseData());
                            break;
                        case SESSION_EXPIRED:
                            mReauthRequired.setValue(true);
                            break;
                        case ERROR:
                            mDevices.setValue(null);
                            break;
                    }
                }
        );
    }
}