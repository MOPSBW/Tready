//
//  ServiceClient+Security.swift
//  Tready
//
//  Created by Brian Krupp on 4/9/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class SecurityModel : Model  {
    public typealias AuthenticateResponseCompletionHandler = (UserId) -> Void
    
    func authenticate(username:String, password:String, completionHandler:@escaping AuthenticateResponseCompletionHandler, errorHandler:@escaping ErrorCompletionHandler) -> Void {
        let serviceRequest:AuthenticationRequest = AuthenticationRequest(username: username, password: password)
        
        self.serviceClient.callPostService(serviceBroker: "security", serviceName: "authenticate", serviceRequest: serviceRequest, serviceResponse: AuthenticateResponse.self, completionHandler: { (response) in
            
            if let authResponse = response as? AuthenticateResponse {
                completionHandler(authResponse.responseData)
            }
            else {
                errorHandler(super.createModelError(model: String(describing: self)))
            }
          
        })
        { (error) in
            errorHandler(error)
        }
    }
}
