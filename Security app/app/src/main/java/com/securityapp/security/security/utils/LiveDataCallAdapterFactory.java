package com.securityapp.security.security.utils;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.securityapp.security.security.service.network.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by Tyler on 2/17/2018.
 */

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if(getRawType(returnType) != LiveData.class){
            return null;
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);

        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("Type must be of resource");
        }

        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("resource must be parameterized");
        }

        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<>(bodyType);
    }
}