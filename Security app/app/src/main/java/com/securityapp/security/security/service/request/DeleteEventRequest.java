package com.securityapp.security.security.service.request;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by Tyler on 1/22/2018.
 */

public class DeleteEventRequest extends TreadyRequest{

    @Expose(serialize = false)
    private static final String servicePath = "/event/DeleteEvent";

    public int eventId;

    public DeleteEventRequest(int eventId){
        this.eventId = eventId;
    }

    @Override
    public HashMap<String, String> getRequestParams() {
        return super.generateRequestParams(servicePath, this);
    }
}