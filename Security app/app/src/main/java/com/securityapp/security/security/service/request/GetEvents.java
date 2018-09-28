package com.securityapp.security.security.service.request;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by Tyler on 1/22/2018.
 */

public class GetEvents extends TreadyRequest {

    @Expose(serialize = false)
    public final String servicePath = "/event/getEvents";
    public String serviceRequest;

    @Override
    public HashMap<String, String> getRequestParams() {
        return super.generateRequestParams(servicePath, this);
    }
}