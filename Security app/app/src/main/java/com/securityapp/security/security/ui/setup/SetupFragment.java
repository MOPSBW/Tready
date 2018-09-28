package com.securityapp.security.security.ui.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.repository.EndpointRepository;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.network.WebserviceProvider;
import com.securityapp.security.security.ui.login.LoginFragment;
import com.securityapp.security.security.ui.main.MainActivity;
import com.securityapp.security.security.utils.SharedPreferencesManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tyler on 2/24/2018.
 */

//TODO: Define endpoints and how to store/dynamically assign to webservices.
public class SetupFragment extends Fragment implements EndpointRepository.EndpointListener, Injectable {

    private String mBaseUrl;

    @BindView(R.id.edit_txt_endpoint)
    TextView endpointInput;

    @Inject
    WebserviceProvider webserviceProvider;

    @Inject
    EndpointRepository repository;

    @Inject
    SharedPreferencesManager preferencesManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.btn_check_endpoint)
    public void checkEnpointButtonClicked(){
    String endpoint = endpointInput.getText().toString();
        if(endpoint.length() > 0){
        if(URLUtil.isValidUrl(endpointInput.getText().toString())){
            if(!endpoint.endsWith("/")){
                endpoint = endpoint + "/";
            }
            //Store an instance of the URL
            mBaseUrl = endpoint;
            webserviceProvider.updateRetrofitURL(endpoint);
            repository.PingEndpoint(this);
        }
        }else{
            Toast.makeText(getContext(), "Cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void endpointValidResults(boolean isValid) {
        Toast.makeText(getContext(), String.valueOf(isValid), Toast.LENGTH_SHORT).show();
        if(isValid){
            storeEndpoint();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, new LoginFragment())
                    .commit();
        }
    }

    private void storeEndpoint(){
        preferencesManager.put(SharedPreferencesManager.Key.ENDPOINT,mBaseUrl);
    }
}
