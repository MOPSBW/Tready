//
//  ServiceClient+Requests.swift
//  Tready
//
//  Created by Brian Krupp on 5/16/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

extension ServiceClient {
    public typealias ResponseCompletionHandler = (Decodable) -> Void
    public typealias GetResponseCompletionHandler = (Data) -> Void
    public typealias ErrorCompletionHandler = (Error) -> Void
    public typealias DownloadCompletionHandler = (URL) -> Void
    
    /*
     The idea here is to pass a request that is encodable and the TYPE of a response that is decodable. When decoding, this allows us to use calling the post service
     generically instead of
     */
    
    public func download(url:URL, delegate:URLSessionDownloadDelegate, completionHandler:@escaping DownloadCompletionHandler) {
        self.downloadSession = URLSession(configuration: self.session.configuration, delegate: delegate, delegateQueue: nil)
        
        let task = self.downloadSession.downloadTask(with: url) { (url, response, error) in
            if let url = url {
                completionHandler(url)
            }
        }
    
        task.resume()
    }
    
    // No Service Request Passed (e.g. /event/getEvents)
    public func callPostService<DecodableResponse:Decodable>(serviceBroker:String, serviceName:String, serviceResponse:DecodableResponse.Type, completionHandler:@escaping ResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) {
        
        // Pass a blank dictionary
        self.callPostService(serviceBroker: serviceBroker, serviceName: serviceName, serviceRequest: Dictionary<String, String>(), serviceResponse: serviceResponse, completionHandler: completionHandler, errorHandler: errorHandler);
    }
    
    // Service Request Passed
    public func callPostService<EncodableRequest:Encodable, DecodableResponse:Decodable>(serviceBroker:String, serviceName:String, serviceRequest:EncodableRequest, serviceResponse:DecodableResponse.Type, completionHandler:@escaping ResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) {
        
        var request = URLRequest(url: self.url)
        request.httpMethod = "POST"
        
        let requestData = createRequestData(serviceBroker: serviceBroker, serviceName: serviceName, serviceRequest:serviceRequest)
        
        guard requestData != nil else {
            let someError = NSError(domain: "Data Request", code: -1, userInfo: ["message":"Unable to create data request."])
            errorHandler(someError)
            return
        }
        
        let httpRequest = self.session.uploadTask(with: request, from: requestData) { (data, response, error) in
            guard error == nil else {
                errorHandler(error!)
                return
            }
            
            // Check if we need to reauth
            if (self.isReauthRequired(responseData: data, serviceName: serviceName)) {
                self.reauth {
                    self.callPostService(serviceBroker: serviceBroker, serviceName: serviceName, serviceRequest: serviceRequest, serviceResponse: serviceResponse, completionHandler: completionHandler, errorHandler: errorHandler)
                }
            }
            else {
                let decoder:JSONDecoder = JSONDecoder()
                do {
                    let decoded = try decoder.decode(serviceResponse, from: data!)
                    completionHandler(decoded)
                    
                }
                catch {
                    let someError = NSError(domain: "Data Request", code: -1, userInfo: ["message":"Unable to create decode response."])
                    errorHandler(someError)
                }
            }
            
        }
        
        httpRequest.resume()
    }
    
    
    private func isReauthRequired(responseData:Data?, serviceName:String) -> Bool {
        // Make sure we don't have a cycle
        if (serviceName == "authenticate") {
            return false
        }
        
        // We should have a response
        if let theResponse = responseData {
            let decoder:JSONDecoder = JSONDecoder()
            do {
                let response = try decoder.decode(Response.self, from: theResponse)
                return response.reauthRequest
            }
            catch {
                // It nay not be a repsonse object and just may be a video or an image from a GET, so return false
                return false
            }
        }
        else {
            // If we didn't get a response, we should return true (we should always have a response)
            return true
        }
    }
    
    private func reauth(completionHandler:@escaping () -> Void) {
        let authRequest = AuthenticationRequest(username: self.username, password: self.password)
        self.callPostService(serviceBroker: "security", serviceName: "authenticate", serviceRequest: authRequest, serviceResponse: AuthenticateResponse.self, completionHandler: { (decodable) in
            completionHandler()
        }) { (error) in
            completionHandler()
        }
    }
    
    // Get service just returns raw data, used primarily for getting photos or videos
    public func callGetService(getParamString: String, completionHandler:@escaping GetResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) {
        let getUrlString = self.url.absoluteString + "?" + getParamString
        let getUrl:URL = URL(string: getUrlString)!
        
        let httpRequest = self.session.dataTask(with: getUrl) { (data, response, error) in
            if (data == nil || self.isReauthRequired(responseData: data, serviceName: "")) {
                self.reauth {
                    self.callGetService(getParamString: getParamString, completionHandler: completionHandler, errorHandler: errorHandler)
                }
            }
            else {
                if (error != nil) {
                    errorHandler(error!)
                }
                else {
                    completionHandler(data!)
                }
            }
        }
        
        httpRequest.resume()
    }
    
    public func getQueryURL(queryString:Dictionary<String, String>) -> URL {
        var getUrlString = self.url.absoluteString + "?";
        
        var keyCount:Int = 0
        for (key, value) in queryString {
            getUrlString = getUrlString + key + "=" + value
            keyCount = keyCount + 1
            if (keyCount != queryString.keys.count) {
                getUrlString = getUrlString + "&"
            }
        }
        
        let getUrl:URL = URL(string: getUrlString)!
        return getUrl
    }
    
    // Take in a generic type that is encodable to create reuqest data
    private func createRequestData<EncodableRequest:Encodable>(serviceBroker:String, serviceName:String, serviceRequest:EncodableRequest) -> Data? {
        var dataRequest = Data()
        
        let requestPreamble = "servicePath=/" + serviceBroker + "/" +  serviceName + "&serviceRequest="
        dataRequest.append(requestPreamble.data(using: .utf8)!)
        
        let jsonEncoder = JSONEncoder()
        do {
            let serviceRequestData = try jsonEncoder.encode(serviceRequest)
            dataRequest.append(serviceRequestData)
            return dataRequest
        }
        catch {
            return nil
        }
    }
}
