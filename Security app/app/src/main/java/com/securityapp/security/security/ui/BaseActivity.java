package com.securityapp.security.security.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.repository.ReauthRepository;
import com.securityapp.security.security.service.request.TreadyCredential;
import com.securityapp.security.security.ui.login.LoginActivity;
import com.securityapp.security.security.ui.reauth.ReauthDialogFragment;
import com.securityapp.security.security.utils.CredentialService;

import javax.inject.Inject;

import butterknife.BindString;

/**
 * BaseActivity implements reauthentication logic for handling expired session IDs
 */

public class BaseActivity extends AppCompatActivity implements ReauthDialogFragment.ReauthDialogListener, ReauthRepository.ReauthRepoListener, Injectable {

    @Inject
    CredentialService credentialService;

    @Inject
    ReauthRepository reauthRepository;

    @BindString(R.string.reauthDialogTitle) String reauthRequiredMsg;

    public void reauthenticationRequired(){
        if(credentialService.getUsername() != null && credentialService.getUserPassword() != null){
            sendReauthRequest(credentialService.getUsername(), credentialService.getUserPassword());
        }else{
            displayReauthDialog();
        }
    }

    /**
     * Displays Reauth dialog prompting for user credentials
     */
    private void displayReauthDialog(){
        ReauthDialogFragment dialog = new ReauthDialogFragment();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), null);
    }

    /**
     * User entered credentials from dialog fragment, send request
     */
    @Override
    public void onReauthCredentialsClicked(String username, String password) {
        sendReauthRequest(username,password);
    }

    /**
     * Sends reauth request with credentials
     */
    private void sendReauthRequest(String username, String password){
        TreadyCredential credential = new TreadyCredential(username,password);
        reauthRepository.reauthRequest(credential, this);
    }

    /**
     * Results from reauth request to webservice
     * @param isValid true if success, false for failure
     */
    @Override
    public void reauthResults(boolean isValid) {
        if(!isValid){
            Toast.makeText(this, reauthRequiredMsg, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}