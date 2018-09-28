package com.securityapp.security.security.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.securityapp.security.security.R;
import com.securityapp.security.security.service.network.WebserviceProvider;
import com.securityapp.security.security.ui.setup.SetupFragment;
import com.securityapp.security.security.utils.NetworkConnection;
import com.securityapp.security.security.utils.SharedPreferencesManager;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * LoginActivity is responsible for determining whether the user has setup their endpoints or not
 * and either displaying the SetupFragment or LoginFragment
 */
public class LoginActivity extends AppCompatActivity implements HasSupportFragmentInjector{

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    SharedPreferencesManager preferencesManager;

    @Inject
    WebserviceProvider webserviceProvider;

    @BindString(R.string.loginFieldsBlankMessage) String loginFieldsBlankMsg;
    @BindString(R.string.authenticatingMsg) String authenticationMsg;
    @BindString(R.string.serverTimeOutMsg) String serverTimeOutMsg;
    @BindString(R.string.loginErrorMsg) String loginErrorMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if(preferencesManager.getString(SharedPreferencesManager.Key.ENDPOINT) == null){
            //No endpoint, go to setup activity
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, new SetupFragment())
                    .commit();
        }else{
            //set Retrofit base url to saved URL in preferences
            webserviceProvider.updateRetrofitURL(preferencesManager.getString(SharedPreferencesManager.Key.ENDPOINT));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}