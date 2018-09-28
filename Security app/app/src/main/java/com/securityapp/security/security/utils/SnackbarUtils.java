package com.securityapp.security.security.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Tyler on 2/18/2018.
 */

public class SnackbarUtils {

    public static void showSnackbar(View v, String snackbarText) {
        if (v == null || snackbarText == null) {
            return;
        }
        Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).show();
    }
}