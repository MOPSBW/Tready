package com.securityapp.security.security.service.request;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Request object for user authentication
 */

public class LoginRequest extends TreadyRequest {

    @Expose(serialize = false)
    public static String servicePath = "/security/authenticate";

    public TreadyCredential serviceRequest;

    public LoginRequest(TreadyCredential serviceRequest){
        this.serviceRequest = serviceRequest;
    }

    @Override
    public HashMap<String, String> getRequestParams() {
        return super.generateRequestParams(servicePath, serviceRequest);
    }
}
