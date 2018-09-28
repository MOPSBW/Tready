package com.securityapp.security.security.service.request;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by Tyler on 2/11/2018.
 */

public class DeviceConfigurationRequest extends TreadyRequest{
    @Expose(serialize = false)
    private String servicePath = "/device/setConfig";
    public long deviceIdentifier;

    @Override
    public HashMap<String, String> getRequestParams() {
        return super.generateRequestParams(servicePath, this);
    }
}
