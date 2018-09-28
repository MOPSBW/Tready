//
//  Model.swift
//  Tready
//
//  Created by Brian Krupp on 5/14/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation
class Model {
    public typealias ErrorCompletionHandler = (Error) -> Void
    
    internal let serviceClient:ServiceClient
    
    init(serviceClient:ServiceClient) {
        self.serviceClient = serviceClient
    }
    
    internal func createModelError(model:String) -> Error {
        let error = NSError(domain: "Model", code: 1, userInfo: ["Unable to convert model" : model])
        return error
    }
}
