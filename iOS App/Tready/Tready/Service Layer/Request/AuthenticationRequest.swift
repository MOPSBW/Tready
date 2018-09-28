//
//  AuthenticationRequest.swift
//  Tready
//
//  Created by Brian Krupp on 5/14/18.
//  Copyright © 2018 Brian Krupp. All rights reserved.
//

import Foundation

class AuthenticationRequest: Encodable {
    let username:String
    let password:String
    
    init(username:String, password:String) {
        self.username = username
        self.password = password
    }
}
