package com.securityapp.security.security.service.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.securityapp.security.security.data.Device;

import java.util.List;

/**
 * Created by Tyler on 1/20/2018.
 */

public class DeviceResponse extends TreadyResponse {

    @SerializedName("responseData")
    @Expose
    private List<Device> responseData = null;

    public List<Device> getResponseData() {
        return responseData;
    }
}
