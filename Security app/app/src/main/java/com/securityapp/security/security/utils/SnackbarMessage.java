package com.securityapp.security.security.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.StringRes;

/**
 * Created by Tyler on 2/18/2018.
 */

public class SnackbarMessage extends SingleLiveEvent<Integer> {

    public void observe(LifecycleOwner owner, final SnackbarObserver observer){
        super.observe(owner, (Integer t) -> {
            if (t == null) {
                return;
            }
            observer.onNewMessage(t);
        });
    }

    public interface SnackbarObserver{
        void onNewMessage(@StringRes int snackbarMessageResourceId);
    }
}
