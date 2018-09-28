package com.securityapp.security.security.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.securityapp.security.security.R;

/**
 * Created by Tyler Rupert on 8/19/2017.
 * Common dialogs that can be used throughout the UI's
 */

public final class AlertDialogUtils {

    public AlertDialogUtils() {
    }

    /**
     * Displays statement dialog for user text input with positive/negative buttons
     *
     * @param activity calling activity
     * @param msg      Message displayed on dialog
     * @param input    EditText instance
     * @param listener Listener for button clicks
     */
    public static void showInputDialog(Activity activity,String title, String msg, EditText input, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title)
                .setMessage(msg)
                .setView(input)
                .setPositiveButton("Confirm", listener)
                .setNegativeButton(R.string.cancel, listener);
        alert.show();
    }

    /**
     * Displays confirmation dialog
     *
     * @param context  current context
     * @param title    Title of dialog box
     * @param msg      Message of dialog box
     * @param listener Listener for button clicks
     */
    public static void showAlertDialog(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Confirm", listener)
                .setNegativeButton(R.string.cancel, listener);
        alert.show();
    }
}