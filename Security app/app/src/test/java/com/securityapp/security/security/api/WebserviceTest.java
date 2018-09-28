package com.securityapp.security.security.api;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.securityapp.security.security.data.Device;
import com.securityapp.security.security.data.User;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.response.DeviceResponse;
import com.securityapp.security.security.service.response.EventResponse;
import com.securityapp.security.security.utils.LiveDataCallAdapterFactory;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.securityapp.security.security.util.LiveDataTestUtil.getValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Tyler on 3/1/2018.
 */

@RunWith(JUnit4.class)
public class WebserviceTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Webservice webservice;

    private MockWebServer mockWebServer;

    @Before
    public void setupService() throws IOException{
        mockWebServer = new MockWebServer();
        webservice = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(Webservice.class);
    }

    @After
    public void stopWebservice() throws IOException{
        mockWebServer.shutdown();
    }

    @Test
    public void loginUser() throws IOException, InterruptedException {
        enqeueResponse("user-valid.json");
        User agent = getValue(webservice.authenticateUser(Collections.emptyMap())).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/service.php"));

        assertThat(agent, notNullValue());
        assertThat(agent.userId, is(0));
        assertThat(agent.responseCode, is(0));
        assertThat(agent.reauthRequest, is(false));
        assertThat(agent.isUserValid(), is(true));
    }

    @Test
    public void getEvents() throws IOException, InterruptedException {
        enqeueResponse("get-events.json");
        EventResponse eventResponse = getValue(webservice.getEvents(Collections.emptyMap())).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/service.php"));

        assertThat(eventResponse.responseCode, is(0));
        assertThat(eventResponse.reauthRequest, is(false));
        assertThat(eventResponse.getResponseData().size(), is(6));
    }

    @Test
    public void getDevices() throws IOException, InterruptedException {
        enqeueResponse("get-devices.json");
        DeviceResponse deviceResponse = getValue(webservice.getDevices(Collections.emptyMap())).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/service.php"));

        assertThat(deviceResponse.reauthRequest, is(false));
        assertThat(deviceResponse.responseCode, is(0));
        assertThat(deviceResponse.getResponseData().size(), is(2));

        Device device = deviceResponse.getResponseData().get(0);
        assertEquals(202481596673632L, device.getDeviceIdentifier());
        assertThat(device.getIpAddress(), is("192.168.1.100"));
        assertThat(device.isEnabled(), is(false));
        assertThat(device.getDeviceName(), is("OfficeCam"));
        assertThat(device.getLastContact(), is("Feb 25, 2018"));
        assertThat(device.getOrientation(), is("landscapePowerUp"));
    }

    private void enqeueResponse(String filename) throws IOException{
        enqeueResponse(filename, Collections.emptyMap());
    }

    /**
     * Enqeues mock response for testing given json file
     * @param filename path to json file
     * @param headers Map<string,string> of headers
     * @throws IOException if incorrect path
     */
    private void enqeueResponse(String filename, Map<String,String> headers) throws IOException{
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api-response/" + filename);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();

        for(Map.Entry<String,String> header : headers.entrySet()){
            mockResponse.addHeader(header.getKey(), header.getValue());
        }

        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
