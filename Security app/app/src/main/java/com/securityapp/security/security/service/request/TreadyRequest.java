package com.securityapp.security.security.service.request;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Base request object
 */

public abstract class TreadyRequest {

    private static final String SERVICE_PATH = "servicePath";
    private static final String SERVICE_REQUEST = "serviceRequest";
    private static Gson mGson = new Gson();

    HashMap<String, String> generateRequestParams(String servicePath, Object object){
        HashMap<String,String> params = new HashMap<>();
        params.put(SERVICE_PATH, servicePath);
        params.put(SERVICE_REQUEST, mGson.toJson(object));
        return params;
    }

    public abstract HashMap<String, String> getRequestParams();
}
