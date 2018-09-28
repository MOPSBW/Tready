package com.securityapp.security.security.service.request;

/**
 * Created by Tyler on 2/6/2018.
 */

public class DeviceNameRequest extends DeviceConfigurationRequest{
    public DeviceNameKeyValuePair configKeyValuePairToUpdate;

    public DeviceNameRequest(long deviceId, String name){
        super.deviceIdentifier = deviceId;
        configKeyValuePairToUpdate = new DeviceNameKeyValuePair(name);
    }

    public class DeviceNameKeyValuePair{
        public String deviceName;
        public DeviceNameKeyValuePair(String deviceName){ this.deviceName = deviceName;}
    }
}