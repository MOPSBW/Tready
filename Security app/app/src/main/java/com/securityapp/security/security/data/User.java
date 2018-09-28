package com.securityapp.security.security.data;

import com.google.gson.annotations.SerializedName;
import com.securityapp.security.security.service.response.TreadyResponse;

/**
 * Created by Tyler on 1/11/2018.
 */

public class User extends TreadyResponse {

    @SerializedName("userId")
    public int userId;

    public boolean isUserValid(){
        return (!reauthRequest && responseCode == 0);
    }
}