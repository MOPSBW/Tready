package com.securityapp.security.security.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.securityapp.security.security.service.network.Resource;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.request.DeviceConfigurationRequest;
import com.securityapp.security.security.service.request.GetDevicesRequest;
import com.securityapp.security.security.service.response.DeviceConfigResponse;
import com.securityapp.security.security.service.response.DeviceResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tyler on 1/20/2018.
 */
@Singleton
public class DeviceRepository {

    private Webservice mWebservice;

    @Inject
    public DeviceRepository(Webservice webservice){
        mWebservice = webservice;
    }

    /**
     * Creates network request to get all devices connected to the PiHub
     * @return List of devices
     */
    public LiveData<Resource<DeviceResponse>> getDevices(){
        MediatorLiveData<Resource<DeviceResponse>> deviceResponse = new MediatorLiveData<>();

        GetDevicesRequest request = new GetDevicesRequest();

        deviceResponse.addSource(mWebservice.getDevices(request.getRequestParams()), apiResponse -> {
            if(apiResponse.isSuccessful()){
                deviceResponse.setValue(Resource.success(apiResponse.getData()));
            }else{
                deviceResponse.setValue(Resource.error(apiResponse.errorMessage, null));
            }
        });
        return deviceResponse;
    }

    /**
     * Sends network request for all device configuration settings.
     * @param configRequest DeviceConfigurationRequest
     * @return DeviceConfigResponse
     */
    public LiveData<Resource<DeviceConfigResponse>> setDeviceConfig1(DeviceConfigurationRequest configRequest){
        MediatorLiveData<Resource<DeviceConfigResponse>> configResponse = new MediatorLiveData<>();

        configResponse.addSource(mWebservice.setDeviceConfiguration(configRequest.getRequestParams()), apiResponse ->{
            if(apiResponse.isSuccessful()){
                configResponse.setValue(Resource.success(apiResponse.getData()));
            }else{
                configResponse.setValue(Resource.error(apiResponse.errorMessage, null));
            }
        });
        return configResponse;
    }
}