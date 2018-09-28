package com.securityapp.security.security.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.securityapp.security.security.data.User;
import com.securityapp.security.security.service.network.Webservice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;

/**
 * Created by Tyler on 3/1/2018.
 */

@RunWith(JUnit4.class)
public class LoginRepositoryTest {
    private LoginRepository loginRepository;
    private Webservice webservice;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void setup(){
        webservice = mock(Webservice.class);
        loginRepository = new LoginRepository(webservice);
    }

    @Test
    public void validCredentials(){
        User user = new User();
        user.userId = 1;
        user.reauthRequest = false;
        user.responseCode = 0;
    }

    @Test
    public void invalidCredentials(){
        //TODO: Make sure invalid session is parsed correctly.
    }
}
