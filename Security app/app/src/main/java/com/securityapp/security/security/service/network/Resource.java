package com.securityapp.security.security.service.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.securityapp.security.security.service.network.Status.ERROR;
import static com.securityapp.security.security.service.network.Status.LOADING;
import static com.securityapp.security.security.service.network.Status.SESSION_EXPIRED;
import static com.securityapp.security.security.service.network.Status.SUCCESS;

/**
 * Created by Tyler on 1/21/2018.
 */

public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final String message;

    @Nullable
    public final T data;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }

    public static <T> Resource<T> sessionExpired(String msg){
        return new Resource<>(SESSION_EXPIRED, null, null);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
