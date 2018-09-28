package com.securityapp.security.security.service.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tyler on 2/4/2018.
 */

public class DeviceConfigResponse extends TreadyResponse {

    @SerializedName("responseData")
    @Expose
    private String responseData;

    public boolean isSuccessful(){
        return responseCode==0;
    }
}
