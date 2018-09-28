package com.securityapp.security.security.service.request;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Request object for getting all devices
 */

public class GetDevicesRequest extends TreadyRequest {

    @Expose(serialize = false)
    public final String servicePath = "/device/getDevices";
    public String serviceRequest;

    @Override
    public HashMap<String, String> getRequestParams() {
        return super.generateRequestParams(servicePath, this);
    }
}
