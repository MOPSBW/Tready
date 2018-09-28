package com.securityapp.security.security.service.request;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by Tyler on 1/22/2018.
 */

public class DeleteEventsRequest extends TreadyRequest {

    @Expose(serialize = false)
    private static final String servicePath = "/event/DeleteEvents";

    public int[] eventIds;

    public DeleteEventsRequest(int[] eventIds){
        this.eventIds = eventIds;
    }

    @Override
    public HashMap<String, String> getRequestParams() {
        return super.generateRequestParams(servicePath, this);
    }
}