package com.securityapp.security.security.ui.reauth;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.ui.login.LoginActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ReauthDialogFragment displays dialog for the user to enter credentials when their
 * session has expired
 */

public class ReauthDialogFragment extends DialogFragment {

    @BindView(R.id.dialog_username_txt) EditText mUsername;
    @BindView(R.id.dialog_password_txt) EditText mPassword;
    @BindString(R.string.loginFieldsBlankMessage) String inputFieldEmptyMsg;

    private ReauthDialogListener mListener;

    public interface ReauthDialogListener {
        void onReauthCredentialsClicked(String username, String password);
    }

    public ReauthDialogFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_credentials, container, false);
        ButterKnife.bind(this,view);

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    @OnClick(R.id.dialog_confirm_btn)
    public void confirmButtonClicked(){
        //If fields are filled out, return entry
        if(mUsername.getText().length() > 0 && mPassword.getText().length() > 0){
            mListener.onReauthCredentialsClicked(mUsername.getText().toString(), mPassword.getText().toString());
            this.dismiss();
        }else{
            resetInput();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mListener == null){
            mListener = (ReauthDialogListener) getActivity();
        }
    }

    @OnClick(R.id.dialog_exit_btn)
    public void exitButtonClicked(){
        startActivity(new Intent(getActivity(), LoginActivity.class));
        this.dismiss();
    }

    private void resetInput(){
        mUsername.setText(null);
        mPassword.setText(null);
        mUsername.requestFocus();
        Toast.makeText(getActivity(), inputFieldEmptyMsg, Toast.LENGTH_SHORT).show();
    }
}