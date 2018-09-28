//
//  ServicecClient.swift
//  Tready
//
//  Created by Brian Krupp on 4/6/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class ServiceClient {    
    internal var url: URL
    internal var session: URLSession
    internal var downloadSession: URLSession
    private static var serviceClient: ServiceClient?
    
    internal var username:String
    internal var password:String
    
    public let credential:URLCredential
    public let protectionSpace:URLProtectionSpace
    
    private init(url: URL, username:String, password:String) {
        self.url = url
        self.username = username
        self.password = password
        self.session = URLSession(configuration: .default)
        
        self.credential = URLCredential(user: self.username, password: self.password, persistence: .forSession)
        self.protectionSpace = URLProtectionSpace(host: url.host!, port: 443, protocol: "https", realm: "Username and password required", authenticationMethod: NSURLAuthenticationMethodHTTPBasic)
        
        self.session.configuration.urlCredentialStorage?.set(self.credential, for: self.protectionSpace)
        
        self.downloadSession = session
        
        URLCredentialStorage.shared.setDefaultCredential(credential, for: protectionSpace)
        
    }

    public static func initSharedInstance(url: URL, username:String, password:String) {
        serviceClient = ServiceClient(url: url, username: username, password: password)
    }
    
    public static func getSharedInstance() -> ServiceClient {
        return ServiceClient.serviceClient!
    }
    
}


