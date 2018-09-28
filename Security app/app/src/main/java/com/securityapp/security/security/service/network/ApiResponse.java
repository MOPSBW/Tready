package com.securityapp.security.security.service.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.securityapp.security.security.service.response.TreadyResponse;

import retrofit2.Response;

/**
 * Created by Tyler on 2/17/2018.
 */

public class ApiResponse<T extends TreadyResponse> {

    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;

    public ApiResponse(Throwable throwable){
        code = 500;
        body = null;
        errorMessage = throwable.getMessage();
    }

    public ApiResponse(Response<T> response){
        code = response.code();
        if(response.isSuccessful()){
            body = response.body();
            errorMessage = null;
        }else{

            String message = null;
            if(response.errorBody() != null){
                message = response.errorBody().toString();
            }

            if(message == null || message.trim().length() == 0){
                message = response.message();
            }

            errorMessage = message;
            body = null;
        }
    }

    public boolean isSuccessful(){
        return (code >=200 && code <300 && (body != null ? body.responseCode : 0) == 0);
    }

    public boolean requiresReauth(){
        return body == null || body.reauthRequest;
    }

    @NonNull
    public T getData() {
        if (body == null) {
            throw new IllegalStateException("Data is null; Call ApiResponse isSuccessful() first.");
        }
        return body;
    }
}