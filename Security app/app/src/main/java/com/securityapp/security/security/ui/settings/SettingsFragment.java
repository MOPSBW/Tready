package com.securityapp.security.security.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.ui.BaseActivity;
import com.securityapp.security.security.ui.login.LoginActivity;
import com.securityapp.security.security.utils.AlertDialogUtils;
import com.securityapp.security.security.utils.CredentialService;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by Tyler Rupert on 8/20/2017.
 */

//TODO: Finish implementing logic and tie into application based on contract
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener, Injectable {

    public static final String PREF_SERVICE_ENDPOINT = "service_endpoint_key";
    public static final String PREF_SIGN_OUT = "sign_out_key";

    @BindString(R.string.signOutMsg) String signOutMsg;
    @BindString(R.string.confirmationMessage) String confirmationMsg;
    @BindString(R.string.settings_title) String activityTitle;

    @Inject
    CredentialService credentialService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        //Without this the preference UI is hard to read because it adopts our custom theme
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        ButterKnife.bind(this,getActivity());

        setupToolbar();

        Preference signOut = findPreference(PREF_SIGN_OUT);
        Preference serviceEndpoint = findPreference(PREF_SERVICE_ENDPOINT);

        signOut.setOnPreferenceClickListener(this);
        serviceEndpoint.setOnPreferenceClickListener(this);
    }

    protected void setupToolbar(){
        if (getActivity() != null && getActivity() instanceof BaseActivity ) {
            android.support.v7.app.ActionBar supportActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
            supportActionBar.setTitle(activityTitle);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case (PREF_SIGN_OUT):
                AlertDialogUtils.showAlertDialog(getActivity(), signOutMsg, confirmationMsg, (dialogInterface, which) -> {
                    if (which == -1) {
                        credentialService.removeUserCredentials();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        dialogInterface.dismiss();
                    }
                });
                break;
            case (PREF_SERVICE_ENDPOINT):
                Toast.makeText(getActivity(), "Service endpoint pressed", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}