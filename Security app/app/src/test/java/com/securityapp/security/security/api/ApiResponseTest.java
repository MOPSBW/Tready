package com.securityapp.security.security.api;

import com.securityapp.security.security.service.network.ApiResponse;
import com.securityapp.security.security.util.MockApiResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created by Tyler on 3/1/2018.
 */

@RunWith(JUnit4.class)
public class ApiResponseTest {

    @Test
    public void exception(){
        Exception exception = new Exception("Whoops!");
        ApiResponse apiResponse = new ApiResponse(exception);

        assertThat(apiResponse.code, is(500));
        assertThat(apiResponse.errorMessage, is("Whoops!"));
        assertThat(apiResponse.body, nullValue());
        assertThat(apiResponse.isSuccessful(), is(false));
    }

    @Test
    public void success(){
        MockApiResponse mockApiResponse = new MockApiResponse();
        mockApiResponse.data = "Doing big things!";
        mockApiResponse.reauthRequest = false;
        mockApiResponse.responseCode = 0;

        ApiResponse apiResponse = new ApiResponse(Response.success(mockApiResponse));

        assertThat(apiResponse.code, is(200));
        assertThat(apiResponse.body.toString(), is("Doing big things!"));
        assertThat(apiResponse.errorMessage, nullValue());
        assertThat(apiResponse.isSuccessful(), is(true));
        assertThat(apiResponse.requiresReauth(), is(false));
    }

    @Test
    public void error(){
        ApiResponse apiResponse = new ApiResponse(Response.error(400, ResponseBody.create(MediaType.parse("application/txt"), "Bob Saget!")));

        assertThat(apiResponse.code, is(400));
        //assertThat(apiResponse.errorMessage, is("Bob Saget!"));
        assertThat(apiResponse.isSuccessful(), is(false));
    }

    @Test
    public void reauth(){
        MockApiResponse mockApiResponse = new MockApiResponse();
        mockApiResponse.data = "Please Authenticate";
        mockApiResponse.responseCode = -1;
        mockApiResponse.reauthRequest = true;

        ApiResponse apiResponse = new ApiResponse(Response.success(mockApiResponse));

        assertThat(apiResponse.code, is(200));
        assertThat(apiResponse.requiresReauth(), is(true));
        assertThat(apiResponse.isSuccessful(), is(false));
    }
}
