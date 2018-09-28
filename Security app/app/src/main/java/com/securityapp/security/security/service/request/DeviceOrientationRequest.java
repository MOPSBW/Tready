package com.securityapp.security.security.service.request;

/**
 * Created by Tyler on 2/11/2018.
 */

public class DeviceOrientationRequest extends DeviceConfigurationRequest {
    public DeviceOrienationKeyValuePair configKeyValuePairToUpdate;

    public DeviceOrientationRequest(long deviceIdentifier, String deviceOrientation){
        super.deviceIdentifier = deviceIdentifier;
        configKeyValuePairToUpdate = new DeviceOrienationKeyValuePair(deviceOrientation);
    }

    public class DeviceOrienationKeyValuePair{
        public String orientation;
        public DeviceOrienationKeyValuePair(String deviceOrientation){
            this.orientation = deviceOrientation;
        }
    }
}