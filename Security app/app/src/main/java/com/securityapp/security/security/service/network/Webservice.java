package com.securityapp.security.security.service.network;

import android.arch.lifecycle.LiveData;

import com.securityapp.security.security.config.Config;
import com.securityapp.security.security.data.User;
import com.securityapp.security.security.service.response.DeleteEventResponse;
import com.securityapp.security.security.service.response.DeleteEventsResponse;
import com.securityapp.security.security.service.response.DeviceConfigResponse;
import com.securityapp.security.security.service.response.DeviceResponse;
import com.securityapp.security.security.service.response.EndpointResponse;
import com.securityapp.security.security.service.response.EventResponse;
import com.securityapp.security.security.service.response.LastEventResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Retrofit network calls
 */

public interface Webservice {

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    LiveData<ApiResponse<User>> authenticateUser(@FieldMap Map<String, String> serviceRequest);

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    LiveData<ApiResponse<EventResponse>> getEvents(@FieldMap Map<String, String> serviceRequest);

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    LiveData<ApiResponse<DeleteEventResponse>> deleteEvent(@FieldMap Map<String, String> serviceRequest);

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    LiveData<ApiResponse<DeleteEventsResponse>> deleteEvents(@FieldMap Map<String, String> serviceRequest);

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    LiveData<ApiResponse<DeviceResponse>> getDevices(@FieldMap Map<String, String> serviceRequest);

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    LiveData<ApiResponse<DeviceConfigResponse>> setDeviceConfiguration(@FieldMap Map<String,String> serviceRequest);

    @FormUrlEncoded
    @POST(Config.SERVICE_POST_URL)
    Call<User> reauthUser(@FieldMap Map<String, String> serviceRequest);

    @POST(Config.SERVICE_POST_URL)
    Call<EndpointResponse> verifyEndpoint();

    @FormUrlEncoded
    @POST(Config.SERVICE_LAST_EVENT_URL)
    LiveData<ApiResponse<LastEventResponse>> getLastEvent();
}