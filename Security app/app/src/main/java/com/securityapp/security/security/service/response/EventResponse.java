package com.securityapp.security.security.service.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.securityapp.security.security.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 1/15/2018.
 */

public class EventResponse extends TreadyResponse{

    @SerializedName("responseData")
    @Expose
    private List<Event> responseData = new ArrayList<>();

    public List<Event> getResponseData() {
        return responseData;
    }
}
