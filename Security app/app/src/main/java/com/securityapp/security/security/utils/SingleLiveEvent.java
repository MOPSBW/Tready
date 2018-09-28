package com.securityapp.security.security.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows for single event subscriptions
 */

public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private static final String TAG = "SingleLiveEvent";

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<T> observer){
        if(hasActiveObservers()){
            Log.w(TAG, "Multiple observers register but only one will be notified of changes.");
        }

        // Observer the internal MutableLiveData
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }


    @MainThread
    public void setValue(T value) {
        mPending.set(true);
        super.setValue(value);
    }

    //Used for cases where T is void, to make calls cleaner
    @MainThread
    public void call(){
        setValue(null);
    }
}
