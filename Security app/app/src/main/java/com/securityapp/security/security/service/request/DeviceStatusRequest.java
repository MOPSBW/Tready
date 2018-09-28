package com.securityapp.security.security.service.request;

/**
 * Created by Tyler on 2/1/2018.
 */

public class DeviceStatusRequest extends DeviceConfigurationRequest{

    public DeviceStatusKeyValuePair configKeyValuePairToUpdate;

    public DeviceStatusRequest(long deviceIdentifier, String enabled){
        super.deviceIdentifier = deviceIdentifier;
        configKeyValuePairToUpdate = new DeviceStatusKeyValuePair(enabled);
    }

    public class DeviceStatusKeyValuePair{
        public String deviceEnabled;
        public DeviceStatusKeyValuePair(String deviceEnabled){
            this.deviceEnabled = deviceEnabled;
        }
    }
}