package com.securityapp.security.security.service.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ginahasrouni on 4/6/17.
 */

public class DeleteEventResponse extends TreadyResponse{

    @SerializedName("responseData")
    @Expose
    private boolean responseData;

    public boolean isSuccessful(){
        return responseData;
    }
}