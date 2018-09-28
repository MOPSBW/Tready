package com.securityapp.security.security.service.response;

import android.arch.lifecycle.LiveData;

import com.google.gson.annotations.SerializedName;
import com.securityapp.security.security.service.network.Resource;

public class LastEventResponse extends TreadyResponse {

    @SerializedName("responseData")
    public LiveData<Resource<LastEventResponse>> eventId;
}
