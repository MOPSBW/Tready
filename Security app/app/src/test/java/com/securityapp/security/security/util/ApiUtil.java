package com.securityapp.security.security.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.securityapp.security.security.service.network.ApiResponse;
import com.securityapp.security.security.service.response.TreadyResponse;

import retrofit2.Response;

/**
 * Created by Tyler on 3/1/2018.
 */

public class ApiUtil {
    public static <T extends TreadyResponse> LiveData<ApiResponse<T>> successCall(T data) {
        return createCall(Response.success(data));
    }
    public static <T extends TreadyResponse> LiveData<ApiResponse<T>> createCall(Response<T> response) {
        MutableLiveData<ApiResponse<T>> data = new MutableLiveData<>();
        data.setValue(new ApiResponse<>(response));
        return data;
    }
}