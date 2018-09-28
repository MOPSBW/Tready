package com.securityapp.security.security.util;

import com.securityapp.security.security.service.response.TreadyResponse;

/**
 * This class is created for testing the ApiResponse since type T must extend TreadyResponse
 */

public class MockApiResponse extends TreadyResponse {

    public String data;

    @Override
    public String toString(){
        return data;
    }

}
