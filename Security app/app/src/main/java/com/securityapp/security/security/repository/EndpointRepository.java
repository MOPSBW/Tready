package com.securityapp.security.security.repository;

import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.network.WebserviceProvider;
import com.securityapp.security.security.service.response.EndpointResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndpointRepository implements Injectable{

    private WebserviceProvider mProvider;
    private Webservice mWebservice;

    public interface EndpointListener{
        void endpointValidResults(boolean isValid);
    }

    @Inject
    public EndpointRepository(WebserviceProvider webserviceProvider){
        mProvider = webserviceProvider;
        //mHostSelectionInterceptor = hostSelectionInterceptor;
    }

    public void PingEndpoint(EndpointListener listener){
        mWebservice = mProvider.getService();
        Call<EndpointResponse> call = mWebservice.verifyEndpoint();

        call.enqueue(new Callback<EndpointResponse>() {
            @Override
            public void onResponse(Call<EndpointResponse> call, Response<EndpointResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().ping != 0){
                        listener.endpointValidResults(true);
                    }else{
                        listener.endpointValidResults(false);
                    }
                }else{
                    listener.endpointValidResults(false);
                }
            }

            @Override
            public void onFailure(Call<EndpointResponse> call, Throwable t) {
                listener.endpointValidResults(false);
            }
        });
    }
}
