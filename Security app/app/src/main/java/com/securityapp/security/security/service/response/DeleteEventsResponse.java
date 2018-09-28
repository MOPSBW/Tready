package com.securityapp.security.security.service.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tyler on 1/22/2018.
 */

public class DeleteEventsResponse extends TreadyResponse{

    @SerializedName("responseData")
    @Expose
    private List<Integer> responseData = null;

    public List<Integer> getResponseData() {
        return responseData;
    }
}