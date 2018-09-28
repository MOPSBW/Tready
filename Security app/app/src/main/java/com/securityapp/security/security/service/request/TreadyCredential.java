package com.securityapp.security.security.service.request;

/**
 * Created by Tyler on 1/21/2018.
 */

public class TreadyCredential {

    public String username;
    public String password;

    public TreadyCredential(String username, String password){
        this.username = username;
        this.password = password;
    }
}