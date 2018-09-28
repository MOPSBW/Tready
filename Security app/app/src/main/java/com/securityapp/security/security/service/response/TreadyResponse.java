package com.securityapp.security.security.service.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tyler on 2/18/2018.
 */

public class TreadyResponse {

    @SerializedName("reauthRequest")
    @Expose
    public boolean reauthRequest;

    @SerializedName("responseCode")
    @Expose
    public int responseCode;
}
